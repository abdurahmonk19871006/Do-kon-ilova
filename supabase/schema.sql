-- ============================================================================
-- QurilishMarket — Supabase (Postgres) sxemasi
-- Supabase Dashboard → SQL Editor'ga to'liq joylashtirib, "Run" bosing.
-- Loyiha: kojfuwipjuutbqjsrpcy.supabase.co
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. JADVALLAR (§5'dagi domain modellarga mos)
-- ----------------------------------------------------------------------------

-- auth.users'ni kengaytiradi — Supabase Auth foydalanuvchini o'zi yaratadi,
-- shu jadval ilova uchun qo'shimcha maydonlarni saqlaydi
create table public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  name text not null default '',
  phone text,
  is_admin boolean not null default false,
  created_at timestamptz not null default now()
);

create table public.categories (
  id uuid primary key default gen_random_uuid(),
  name text not null,
  icon_url text,
  parent_id uuid references public.categories(id),
  sort_order int not null default 0
);

create table public.products (
  id uuid primary key default gen_random_uuid(),
  code text unique not null,
  name text not null,
  category_id uuid not null references public.categories(id),
  unit text not null check (unit in ('DONA','KG','METR','LITR','QOP','QUTI','RULON','M2','M3')),
  price bigint not null check (price >= 0),
  old_price bigint check (old_price >= 0),
  stock int not null default 0 check (stock >= 0),
  short_description text default '',
  full_description text default '',
  images text[] not null default '{}',
  is_popular boolean not null default false,
  is_new boolean not null default false,
  is_active boolean not null default true, -- hard-delete o'rniga — buyurtma tarixi buzilmasligi uchun
  created_at timestamptz not null default now()
);

create table public.discounts (
  id uuid primary key default gen_random_uuid(),
  product_id uuid references public.products(id) on delete cascade,
  category_id uuid references public.categories(id) on delete cascade,
  type text not null check (type in ('PERCENT','FIXED')),
  value numeric not null check (value > 0),
  starts_at timestamptz,
  ends_at timestamptz,
  check (product_id is not null or category_id is not null)
);

create table public.addresses (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  title text not null,
  full_address text not null,
  lat double precision,
  lng double precision,
  is_default boolean not null default false
);

create table public.orders (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id),
  status text not null default 'QABUL_QILINDI'
    check (status in ('QABUL_QILINDI','TAYYORLANMOQDA','YETKAZILMOQDA','YETKAZILDI','BEKOR_QILINDI')),
  delivery_type text not null check (delivery_type in ('YETKAZIB_BERISH','OLIB_KETISH')),
  address_id uuid references public.addresses(id),
  payment_method text not null check (payment_method in ('NAQD','PAYME','CLICK','KARTA')),
  comment text,
  subtotal bigint not null default 0,
  delivery_fee bigint not null default 0,
  total bigint not null default 0,
  created_at timestamptz not null default now()
);

-- Massiv emas, haqiqiy jadval — buyurtma tarixini so'rash va hisobotlarni osonlashtiradi
create table public.order_items (
  id uuid primary key default gen_random_uuid(),
  order_id uuid not null references public.orders(id) on delete cascade,
  product_id uuid not null references public.products(id),
  name text not null,     -- buyurtma vaqtidagi snapshot
  price bigint not null,  -- buyurtma vaqtidagi snapshot — narx keyin o'zgarsa ham tarix buzilmaydi
  quantity int not null check (quantity > 0),
  unit text not null
);

create table public.order_status_history (
  id uuid primary key default gen_random_uuid(),
  order_id uuid not null references public.orders(id) on delete cascade,
  status text not null,
  changed_at timestamptz not null default now()
);

create table public.favorites (
  user_id uuid not null references auth.users(id) on delete cascade,
  product_id uuid not null references public.products(id) on delete cascade,
  primary key (user_id, product_id)
);

-- ----------------------------------------------------------------------------
-- 2. INDEKSLAR — qidiruv va filtrlash tezligi uchun (§3'dagi qidiruv talabi)
-- ----------------------------------------------------------------------------

create index idx_products_category on public.products(category_id);
create index idx_products_code on public.products(code);
create index idx_products_search on public.products using gin (to_tsvector('simple', name));
create index idx_orders_user on public.orders(user_id);
create index idx_order_items_order on public.order_items(order_id);
create index idx_addresses_user on public.addresses(user_id);

-- ----------------------------------------------------------------------------
-- 3. YANGI FOYDALANUVCHI UCHUN AVTOMATIK PROFIL (standart Supabase naqshi)
-- ----------------------------------------------------------------------------

create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  insert into public.profiles (id, name, phone)
  values (new.id, coalesce(new.raw_user_meta_data->>'name', ''), new.phone);
  return new;
end;
$$;

create trigger on_auth_user_created
  after insert on auth.users
  for each row execute function public.handle_new_user();

-- ----------------------------------------------------------------------------
-- 4. is_admin'ni O'ZIGA O'ZI BERISHDAN HIMOYA (haqiqiy xavfsizlik shu yerda)
-- ----------------------------------------------------------------------------

create or replace function public.prevent_self_admin_promotion()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  if new.is_admin is distinct from old.is_admin then
    if not exists (select 1 from public.profiles where id = auth.uid() and is_admin = true) then
      raise exception 'is_admin maydonini faqat admin o''zgartira oladi';
    end if;
  end if;
  return new;
end;
$$;

create trigger trg_prevent_self_admin_promotion
  before update on public.profiles
  for each row execute function public.prevent_self_admin_promotion();

-- ----------------------------------------------------------------------------
-- 5. BUYURTMA YARATISH — bitta tranzaksiyada, stock'ni serverda tekshirib
--    (§6'dagi "orders/order_items'ga to'g'ridan-to'g'ri INSERT yo'q" qarori shu yerda amalga oshadi)
-- ----------------------------------------------------------------------------

create or replace function public.create_order(
  p_items jsonb,              -- [{"product_id": "...", "quantity": 2}, ...]
  p_delivery_type text,
  p_address_id uuid,
  p_payment_method text,
  p_comment text,
  p_delivery_fee bigint
)
returns uuid
language plpgsql
security definer
set search_path = public
as $$
declare
  v_order_id uuid;
  v_item jsonb;
  v_product public.products%rowtype;
  v_qty int;
  v_subtotal bigint := 0;
begin
  insert into public.orders (user_id, status, delivery_type, address_id, payment_method, comment, delivery_fee)
  values (auth.uid(), 'QABUL_QILINDI', p_delivery_type, p_address_id, p_payment_method, p_comment, p_delivery_fee)
  returning id into v_order_id;

  for v_item in select * from jsonb_array_elements(p_items)
  loop
    v_qty := (v_item->>'quantity')::int;

    -- FOR UPDATE: shu qatorni qulflaydi — ikki kishi bir vaqtda oxirgi donani
    -- olishga urinsa, ikkinchisi navbatda kutadi va yangilangan qoldiqni ko'radi
    select * into v_product from public.products
      where id = (v_item->>'product_id')::uuid for update;

    if v_product.id is null then
      raise exception 'Mahsulot topilmadi';
    end if;

    if v_product.stock < v_qty then
      raise exception 'Yetarli mahsulot yo''q: %', v_product.name;
    end if;

    update public.products set stock = stock - v_qty where id = v_product.id;

    insert into public.order_items (order_id, product_id, name, price, quantity, unit)
    values (v_order_id, v_product.id, v_product.name, v_product.price, v_qty, v_product.unit);

    v_subtotal := v_subtotal + (v_product.price * v_qty);
  end loop;

  update public.orders
    set subtotal = v_subtotal, total = v_subtotal + p_delivery_fee
    where id = v_order_id;

  insert into public.order_status_history (order_id, status) values (v_order_id, 'QABUL_QILINDI');

  return v_order_id;
end;
$$;

-- ----------------------------------------------------------------------------
-- 6. BUYURTMA STATUSINI O'ZGARTIRISH — faqat admin (funksiya ichida ham tekshiriladi)
-- ----------------------------------------------------------------------------

create or replace function public.update_order_status(p_order_id uuid, p_new_status text)
returns void
language plpgsql
security definer
set search_path = public
as $$
begin
  if not exists (select 1 from public.profiles where id = auth.uid() and is_admin = true) then
    raise exception 'Faqat admin buyurtma statusini o''zgartira oladi';
  end if;

  update public.orders set status = p_new_status where id = p_order_id;
  insert into public.order_status_history (order_id, status) values (p_order_id, p_new_status);
end;
$$;

-- ----------------------------------------------------------------------------
-- 7. ROW LEVEL SECURITY — barcha jadvallarda yoqiladi
-- ----------------------------------------------------------------------------

alter table public.profiles enable row level security;
alter table public.categories enable row level security;
alter table public.products enable row level security;
alter table public.discounts enable row level security;
alter table public.addresses enable row level security;
alter table public.orders enable row level security;
alter table public.order_items enable row level security;
alter table public.order_status_history enable row level security;
alter table public.favorites enable row level security;

-- profiles: o'zinikini yoki admin bo'lsa hammasini o'qiydi; faqat o'zinikini yangilaydi
-- (is_admin'ni o'zgartirishning oldi #4'dagi trigger bilan olinadi)
create policy "profiles_select" on public.profiles for select using (
  auth.uid() = id or exists (select 1 from public.profiles p where p.id = auth.uid() and p.is_admin = true)
);
create policy "profiles_update_own" on public.profiles for update using (auth.uid() = id) with check (auth.uid() = id);

-- categories, products, discounts: hamma o'qiydi, faqat admin yozadi
create policy "categories_select_all" on public.categories for select using (true);
create policy "categories_admin_write" on public.categories for all using (
  exists (select 1 from public.profiles where id = auth.uid() and is_admin = true)
) with check (
  exists (select 1 from public.profiles where id = auth.uid() and is_admin = true)
);

create policy "products_select_all" on public.products for select using (true);
create policy "products_admin_write" on public.products for all using (
  exists (select 1 from public.profiles where id = auth.uid() and is_admin = true)
) with check (
  exists (select 1 from public.profiles where id = auth.uid() and is_admin = true)
);

create policy "discounts_select_all" on public.discounts for select using (true);
create policy "discounts_admin_write" on public.discounts for all using (
  exists (select 1 from public.profiles where id = auth.uid() and is_admin = true)
) with check (
  exists (select 1 from public.profiles where id = auth.uid() and is_admin = true)
);

-- addresses, favorites: har kim faqat o'zinikini ko'radi/boshqaradi
create policy "addresses_own" on public.addresses for all using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "favorites_own" on public.favorites for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

-- orders: o'zinikini yoki admin bo'lsa hammasini o'qiydi.
-- INSERT/UPDATE policy YO'Q — faqat create_order()/update_order_status() orqali (§6)
create policy "orders_select" on public.orders for select using (
  auth.uid() = user_id or exists (select 1 from public.profiles where id = auth.uid() and is_admin = true)
);
create policy "order_items_select" on public.order_items for select using (
  exists (
    select 1 from public.orders o
    where o.id = order_id and (o.user_id = auth.uid() or exists (
      select 1 from public.profiles where id = auth.uid() and is_admin = true
    ))
  )
);
create policy "order_status_history_select" on public.order_status_history for select using (
  exists (
    select 1 from public.orders o
    where o.id = order_id and (o.user_id = auth.uid() or exists (
      select 1 from public.profiles where id = auth.uid() and is_admin = true
    ))
  )
);

-- ----------------------------------------------------------------------------
-- 8. REALTIME — Kuzatuv ekrani buyurtma statusini jonli kuzatishi uchun (§6)
-- ----------------------------------------------------------------------------

alter publication supabase_realtime add table public.orders;
alter publication supabase_realtime add table public.order_status_history;

-- ----------------------------------------------------------------------------
-- 9. CHEGIRMADAGI MAHSULOTLARNI TEZ FILTRLASH UCHUN — generated column
--    (Android tomonda "eq(has_discount, true)" bilan so'raladi, murakkab
--    "IS NOT NULL" filtridan qochish uchun — natija bir xil, so'rov soddaroq)
-- ----------------------------------------------------------------------------

alter table public.products
  add column has_discount boolean generated always as (old_price is not null and old_price > price) stored;

create index idx_products_discounted on public.products(has_discount) where has_discount;

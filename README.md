# QurilishMarket — Android ilovasi

Qurilish va xo'jalik mollari do'koni uchun to'liq Android xarid ilovasi. Arxitektura va
loyihalash tafsilotlari — `loyiha-arxitekturasi.md`. **Backend**: Supabase —
`kojfuwipjuutbqjsrpcy.supabase.co`.

## Ishga tushirish — ikkita yo'l

### A) Kompyuter/Android Studio'siz — GitHub Actions orqali (tavsiya etiladi)

Hisoblashning hammasi GitHub serverida bo'ladi — sizga faqat brauzer kerak.

1. **Ma'lumotlar bazasini tayyorlash**: Supabase Dashboard -> SQL Editor'ga `supabase/schema.sql`
   faylining butun mazmunini joylashtirib, **Run** bosing.
2. GitHub'da yangi repository yarating va shu loyiha fayllarini shu yerga yuklang.
3. Repository -> **Settings -> Secrets and variables -> Actions -> New repository secret** orqali
   ikkita maxfiy qiymat qo'shing: `SUPABASE_URL` va `SUPABASE_PUBLISHABLE_KEY` (qiymatlar
   `local.properties` faylida allaqachon bor - o'sha yerdan nusxalang).
4. Fayllarni yuklashning o'zi build'ni avtomatik ishga tushiradi (`.github/workflows/build-apk.yml`
   tufayli). **Actions** bo'limidan borishini kuzatib turing (bir necha daqiqa).
5. Build tugagach, o'sha workflow sahifasida **Artifacts** ostidan `QurilishMarket-debug-apk`ni
   yuklab oling - bu tayyor `.apk` fayl.
6. APK'ni telefoningizga o'tkazib, ustiga bosib o'rnating (birinchi marta "noma'lum manbalardan
   o'rnatish"ga ruxsat so'rashi mumkin - bu oddiy Android xavfsizlik so'rovi).

### B) Android Studio bilan (kompyuter bo'lsa)

1. Yuqoridagi 1-qadam (schema.sql) - bir xil.
2. Android Studio'da **Open** -> shu papkani tanlang.
3. `local.properties`da `SUPABASE_URL` va `SUPABASE_PUBLISHABLE_KEY` allaqachon to'ldirilgan.
4. Gradle sync qiling va ishga tushiring.

> Versiya raqamlari (Kotlin, Compose BOM, AGP, supabase-kt...) 2026-yil holatiga yaqin.
> "Upgrade" taklif qilinsa - bemalol rozilik bering.

> Loyihada hali `gradlew` fayllari yo'q (standart Gradle wrapper) - shuning uchun CI
> to'g'ridan-to'g'ri Gradle'ni o'rnatadi. Keyinchalik kompyuterga (yoki Codespaces'ga) kirish
> imkoni bo'lsa, `gradle wrapper` buyrug'ini bir marta ishga tushirib, standart holatga
> keltirish mumkin - bu ixtiyoriy tozalash, hozircha shart emas.

## Loyihada nima bor

To'liq mijoz oqimi: **Bosh sahifa -> Kategoriyalar/Qidiruv -> Mahsulot tafsilotlari ->
Savatcha -> Kirish (telefon OTP) -> Buyurtma berish -> Buyurtmalarim -> Buyurtma tafsilotlari
(Realtime bilan jonli status)**. Shuningdek **Profil -> Manzillarim/Sevimlilar/Sozlamalar**.

Va to'liq **admin panel** (Sozlamalar'dagi versiya raqamini 7 marta bosib kiriladi, §11):
Dashboard -> Mahsulotlar (qo'shish/tahrirlash/o'chirish) -> Buyurtmalar (status o'zgartirish).

- **`supabase/schema.sql`** - jadvallar, RLS, `create_order()`/`update_order_status()`
  funksiyalari, avtomatik profil yaratish, admin-himoya trigger'i (§6)
- **96 ta Kotlin fayl** - Clean Architecture: `domain/` (model, repository interfeys, use case),
  `data/` (DTO, mapper, repository implementatsiya), `presentation/` (har bir ekran uchun
  ViewModel + Compose UI), `di/` (Hilt modullari)

> **Diqqat**: Postgrest/Auth/Realtime so'rov sintaksisi supabase-kt hujjatlariga asoslangan,
> lekin bu jadal rivojlanayotgan community kutubxona - birinchi compile'da ba'zi metod nomlari
> biroz boshqacha bo'lishi mumkin (GitHub Actions build xato bersa, log aynan qaysi qatorda
> ekanini aniq ko'rsatadi). Har bir shunday joy kodda "DIQQAT" izohi bilan belgilangan.

> **Telefon OTP ishlashi uchun**: Supabase Dashboard -> Authentication -> Providers -> Phone'da
> SMS provayder (Twilio va h.k.) ulanishi kerak - bepul emas. Test uchun Dashboard'da
> "Test OTP" raqam+kod qo'shib, haqiqiy SMS'siz sinash mumkin.

## Xavfsizlik - bitta narsani unutmang

`local.properties`dagi kalit **publishable/anon** kalit - client'da bo'lishi xavfsiz, hatto
APK dekompilyatsiya qilinsa ham. Lekin Supabase Dashboard'da yana bitta kalit bor -
**`service_role`** - u butun RLS himoyasini chetlab o'tadi. Uni **hech qachon** ilova
kodiga, git'ga, GitHub Secrets'dan boshqa joyga yoki har qanday client-side joyga qo'ymang.

## Keyingi bosqichda keladi

- Payme/Click uchun haqiqiy to'lov integratsiyasi (hozir usul tanlanadi, WebView orqali
  haqiqiy to'lov sahifasi keyin qo'shiladi)
- Rasm yuklash UI'si (hozir admin formada URL qo'lda kiritiladi)
- Push-bildirishnoma: Supabase'da tayyor push yo'q - FCM (faqat yetkazish uchun) +
  Edge Function orqali qo'shiladi (§6). Realtime esa ilova ochiq turganda allaqachon ishlaydi.

### Birinchi admin

`schema.sql` ishga tushirilgandan keyin, birinchi foydalanuvchi (siz) ro'yxatdan o'ting, so'ng
Supabase Dashboard -> Table Editor -> `profiles` jadvalida o'z qatoringizda `is_admin`ni
`true`ga o'zgartiring (bir martalik qo'lda amal - keyingi adminlarni shu birinchi admin
ilovaning o'zidan boshqara olmaydi hali, bu - kelajakdagi kichik kengaytirish).

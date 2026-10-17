# 🍕 Yumrush - Delicious Food Delivered at Warp Speed! 🚀

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android"/>
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose"/>
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase"/>
  <img src="https://img.shields.io/badge/Material_3-7B1FA2?style=for-the-badge&logo=materialdesign&logoColor=white" alt="Material 3"/>
</p>

---

## ✨ Overview

**Yumrush** is a modern, ultra-fast, and sleek **Android Food Ordering & Tracking Application** built entirely with **Jetpack Compose** and powered by **Firebase**. Designed with a stunning dark theme UI, buttery-smooth animations, and a rich user experience, Yumrush turns craving into dining in just a few taps! 😋🔥

---

## 🔥 Key Features

### 🔐 **Seamless Authentication**
- Quick Sign-up and Login powered by **Firebase Authentication**.
- Persistent user session so you stay logged in seamlessly.

### 🍔 **Interactive Menu & Category Discovery**
- Browse food categories (Burgers, Pizza, Drinks, Bakery, etc.) with real-time shimmer loading effects.
- **Dynamic Search & Filtering**: Filter meals by *Best Seller*, *Fast Delivery*, *Flame Grilled*, *Low Calorie*, and *Top Rated*.

### 🛒 **Smart Shopping Cart**
- Real-time cart management powered by local caching (`TinyDB`) and state management.
- Quick quantity increments, decrements, and instant calculation of subtotal, delivery fee, and taxes.

### 💳 **Frictionless Checkout & Saved Addresses**
- Support for multiple payment methods (**Cash on Delivery**, **Credit/Debit Card**, **PayPal**, **UPI**).
- Address Book management (**Home**, **Work**, **Other**) with default address selection.
- Promo code application support.

### 🚚 **Real-Time Order Tracking**
- Track your food journey in real-time with visual status stages:
  1. 📝 **Order Placed**
  2. 👨‍🍳 **Preparing**
  3. 🛵 **Out for Delivery**
  4. 🎉 **Delivered**
- View estimated arrival time and detailed order summaries anytime from **Order History**.

### 🎨 **Modern Dark UI & UX**
- Fully custom dark palette (`#1E1E1E`, `#FF6B00` accents).
- Custom Skeleton Shimmers for effortless visual feedback while fetching data.

---

## 🛠️ Tech Stack & Architecture

Yumrush follows modern Android development best practices and Clean Architecture principles:

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 & Material 2 components.
- **Language**: 100% [Kotlin](https://kotlinlang.org/).
- **Architecture**: **MVVM (Model-View-ViewModel)** with StateFlow & Coroutines.
- **Backend / DB**: [Firebase Realtime Database](https://firebase.google.com/docs/database) & [Firebase Auth](https://firebase.google.com/docs/auth).
- **Image Loading**: [Coil Compose](https://coil-kt.github.io/coil/) for fast, asynchronous image caching.
- **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation).
- **Data Persistence**: Gson + SharedPreferences (`TinyDB`).

---

## 📁 Project Structure

```
com.example.foodordering
├── domain              # Data models (FoodModel, OrderModel, UserModel, AddressModel, etc.)
├── Repository          # Data layer (AuthRepository, MainRepository, OrderRepository, UserRepository)
├── viewModel           # ViewModels handling UI state with StateFlow
├── screens             # Jetpack Compose UI Screens
│   ├── auth            # LoginScreen, SignupScreen
│   ├── cart            # CartScreen
│   ├── checkout        # CheckoutScreen, OrderSuccessScreen
│   ├── dashboard       # MainScreen, CategoryItem, FoodItemCardGrid, TopBar, MyBottomBar
│   ├── detailFood      # DetailScreen, HeaderSection, TitleNumberRow
│   ├── itemsList       # ItemListScreen, ItemsCard
│   ├── orders          # OrderHistoryScreen, OrderDetailScreen
│   ├── profile         # ProfileScreen, SavedAddressesScreen
│   ├── tracking        # OrderTrackingScreen
│   └── common          # ShimmerEffects, StateComposables
├── Helper              # Local storage helpers (ManagementCart, TinyDB)
└── ui/theme            # Custom Material3 Typography, Color Palette, and Theme Setup
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Ladybug (2024.2.1) or newer.
- **JDK 21** configured in Android Studio.
- An active **Firebase Project**.

### Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Nezukochannnnn/Yumrush.git
   cd Yumrush
   ```

2. **Configure Firebase**
   - Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
   - Add an Android App with package name `com.example.foodordering`.
   - Download your `google-services.json` file.
   - Place `google-services.json` into the `app/` directory (`app/google-services.json`). *Note: A template file `app/google-services.json.sample` is provided for reference.*

3. **Build & Run**
   - Open the project in Android Studio.
   - Sync Gradle files.
   - Run the app on an Android Emulator or physical device (Min SDK: 24 / Android 7.0+).

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check out the [issues page](https://github.com/Nezukochannnnn/Yumrush/issues).

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

<p align="center">
  Made with ❤️ for food lovers everywhere! 🍕🍔🍟
</p>

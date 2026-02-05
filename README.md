# TourismMY

Android prototype demonstrating a multi-role tourism platform with Firebase-backed authentication, service listings, and itinerary planning.

> ⚠️ This project is a **functional prototype** built for academic and learning purposes.  
> Core user flows are implemented, while some features are partial or simulated.

## Project Overview

TourismMY is an Android application prototype designed to explore how a single platform could connect tourists, local service providers, and administrators within the Malaysian tourism ecosystem.

The app focuses on:
- Role-based user experiences (Tourist, Business, Admin)
- Firebase-backed authentication and data storage
- A rule-based itinerary generator to assist travel planning
- Basic safety and localization features

## User Roles

- **Tourist**  
  Browse attractions and services, generate itineraries, manage bookings, and access safety features.

- **Business Owner**  
  Create and manage service listings, view incoming bookings, and update availability.

- **Administrator**  
  Monitor platform data, view system metrics, and moderate listings.

## Features

### Authentication & Access Control
- ✔ Firebase Authentication (login & registration)
- ✔ Role-based routing (Tourist / Business / Admin)

### Tourist Features
- ✔ Browse tourism services and attractions
- ◐ Rule-based itinerary generator (interest-based, prototype logic)
- ◐ Booking flow (happy-path only)
- ✖ Real payment processing (simulated in prototype)
- ✔ Emergency call shortcuts (police / ambulance)
- ◐ Language switching (English / Malay, limited scope)

### Business Features
- ◐ Business dashboard for managing listings
- ✔ Firestore-backed listing creation and updates
- ◐ Basic booking visibility (limited validation)

### Admin Features
- ✔ Admin panel for viewing users and bookings
- ✔ Listing moderation via Firestore

## Screenshots

### Tourist (User Side)

| Explore | Itinerary | Help |
|--------|-----------|------|
| ![Explore Page](screenshots/UserSide-ExplorePage.png) | ![Itinerary Page](screenshots/UserSide-ItineraryPage.png) | ![Help Page](screenshots/UserSide-HelpPage.png) |

| Profile | Payment |
|---------|---------|
| ![Profile Page](screenshots/UserSide-ProfilePage.png) | ![Payment Page](screenshots/UserSide-PaymentPage.png) |

---

### Business Side

| Business Portal |
|-----------------|
| ![Business Portal](screenshots/BusinessSide-BusinessPortal.png) |

---

### Admin Side

| Admin Dashboard | Firebase Database |
|-----------------|------------------|
| ![Admin Main Page](screenshots/AdminSide-AdminMainPage.png) | ![Firebase Database](screenshots/AdminSide-FirebaseDatabase.png) |

## Technology Stack

- **Language:** Java
- **Platform:** Android SDK
- **Authentication:** Firebase Authentication
- **Database:** Cloud Firestore
- **UI:** XML layouts with Material Design components
- **Build System:** Gradle

## Architecture Overview

The application follows a straightforward separation of concerns between UI, data models, and control logic.

- **Activities & Fragments** handle user interaction and navigation.
- **Model classes** represent users, listings, bookings, and itineraries.
- **Firebase services** manage authentication and cloud data storage.

Role-based routing is enforced at login, where users are redirected to the appropriate interface (Tourist, Business, or Admin) based on their assigned role in Firestore.

## Data Handling

- User authentication is managed by Firebase Authentication.
- Application data (users, listings, bookings, itineraries) is stored in Cloud Firestore.
- Read and write operations are performed asynchronously to avoid blocking the UI thread.
- Sensitive data such as passwords is never stored locally.

## Limitations

This project is a functional prototype and was built with a limited scope. As such, several features are intentionally incomplete or simplified:

- Payment processing is simulated and does not integrate with real payment gateways.
- Booking and business management flows support only basic validation and happy-path scenarios.
- The itinerary generator uses static, rule-based logic rather than adaptive or data-driven algorithms.
- Multi-language support is limited to English and Malay and is only implemented on selected screens.
- Error handling and edge cases are minimal and would require further refinement for production use.

## Future Improvements

- Integrate real payment gateways (e.g., Stripe or local e-wallets).
- Expand itinerary generation using user behavior or preference-based logic.
- Improve multi-language support across the entire application.
- Add comprehensive input validation and error handling.
- Introduce automated testing for core user flows.






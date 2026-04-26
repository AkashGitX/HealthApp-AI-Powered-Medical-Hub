# HealthApp — AI-Powered Medical Hub

> A full-stack healthcare web application built with Java 17, Spring Boot 3, and Spring Security — offering intelligent health services, role-based dashboards, payment integration, and real-time video consultation support.

---

## Problem Statement

Access to quality healthcare remains fragmented and expensive. Patients struggle to find affordable medicines, book appointments, understand medical reports, and connect with doctors remotely — often across multiple disconnected platforms.

## Solution Overview

HealthApp consolidates essential healthcare services into one unified platform. It provides users with AI-assisted report analysis, cost-effective medicine suggestions, eye and mental health self-assessment tools, appointment booking with integrated payment, and video consultation infrastructure — all secured behind JWT-based, role-aware access control.

---

## Features

### Authentication & Authorization
- JWT-based registration and login for all roles
- Role-based routing: `USER`, `DOCTOR`, `SERVICE_PROVIDER`
- Secure session management with stateless JWT tokens
- Protected API endpoints enforced via Spring Security filter chain

### Landing Page
- Responsive navbar with dynamic authentication state
- Direct entry points to all six core services:
  - Eye Checkup
  - Mental Health Test
  - Report Analyzer
  - Low-Cost Medicine Finder
  - Book Appointment (offline/clinic)
  - Online Consultation (video)
- Mental Health & Manifestation awareness banner
- Doctor directory with availability and specialization display
- Premium three-column footer with contact, features, and platform description

### User Dashboard
- View and track all personal appointments
- Access all healthcare services
- View payment and booking status
- Review saved test results (eye, mental health)

### Doctor Dashboard
- Manage profile: specialization, qualification, hospital, availability
- View incoming appointment requests (offline and online)
- Accept or reject online consultation requests
- Doctor type designation: `OFFLINE` (clinic) or `ONLINE` (virtual)

### Service Provider Dashboard
- Register and manage clinic/pharmacy profile
- Configure: shop name, address, city, state, pincode, timings
- List available doctors and services offered
- Set pricing information for displayed services

### Eye Checkup (No AI)
- Three-stage self-assessment:
  - Visual acuity test (letter-based)
  - Ishihara color blindness test (12 plates, fixed correct answers)
  - Amsler grid distortion test
- Automatic result scoring and pass/fail determination
- Doctor consultation recommendation on poor results
- Results persisted to the database per user

### Mental Health Test (No AI)
- Questionnaire-based psychological self-assessment
- Static server-side scoring logic — no external model involved
- Structured result output with recommendation
- Results saved to user's test history

### Report Analyzer (Controlled AI)
- User uploads a medical report (text-based)
- Fixed prompt sent to OpenAI — no free-form input accepted
- Response simplified to plain-language health explanation
- Designed to never expose raw model responses without structure

### Low-Cost Medicine Finder (Controlled AI)
- User submits a medicine name or prescription
- Fixed AI prompt: suggest cheaper generic alternatives
- Response parsed into structured `MedicineDTO` cards
- Each card includes links to Amazon, Flipkart, 1mg, and PharmEasy
- Loading skeleton UI during AI response processing

### Appointment Booking (Offline Doctors)
- Filtered to `OFFLINE`/`IN_PERSON` doctors only
- Doctor selection with profile, fee, and availability display
- Stripe payment intent creation before booking confirmation
- Appointment saved to database on successful payment
- Appointment type enforced as `IN_PERSON`

### Online Consultation (Online Doctors)
- Filtered to `ONLINE` doctors only
- Patient submits a consultation request
- Doctor accepts or rejects via dashboard
- On acceptance: `roomId` generated and saved to the `Consultation` entity
- ZegoCloud video call room launched via `video-call.html`
- Role passed via URL parameter (`?role=patient` or `?role=doctor`)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.4 |
| Security | Spring Security 6 + JWT (JJWT) |
| ORM | Spring Data JPA (Hibernate 6) |
| Database | PostgreSQL (primary) |
| Build Tool | Maven |
| Templating | Thymeleaf |
| Frontend | HTML, CSS, JavaScript, Bootstrap 5, Bootstrap Icons |
| AI Integration | Spring AI → OpenAI (fixed prompts only) |
| Payment | Stripe (payment intent flow) |
| Video Call | ZegoCloud (room-based, roomId from DB) |
| Utilities | Lombok, Jackson |


---

## System Architecture

```
Browser (Thymeleaf + JS)
        │
        ▼
Spring Security Filter Chain
   └── JwtAuthFilter → validates Bearer token on every request
        │
        ▼
Controllers (REST + MVC)
   ├── AuthController       → /api/auth/login, /api/auth/register
   ├── HomeController       → /, /home, /mental-health
   ├── UserDashboardController   → /dashboard/user
   ├── DoctorDashboardController → /dashboard/doctor
   ├── ServiceProviderDashboardController → /dashboard/provider
   ├── AppointmentController → /api/appointments
   ├── DoctorController     → /api/doctor
   ├── EyeTestController    → /api/eye-test
   ├── TestController       → /api/tests
   ├── AiController         → /api/ai
   ├── PaymentController    → /api/payment
   ├── VideoCallController  → /services/video-call
   └── ServiceProviderController → /api/provider
        │
        ▼
Services (Business Logic)
   └── AuthService, AppointmentService, DoctorService,
       AiService, PaymentService, TestResultService,
       UserService, ServiceProviderService
        │
        ▼
Repositories (Spring Data JPA)
        │
        ▼
PostgreSQL Database
```

**Request flow:**
1. Browser sends request with `Authorization: Bearer <token>` header
2. `JwtAuthFilter` validates the token and loads `UserDetails`
3. Spring Security sets the `SecurityContext`
4. Controller delegates to the appropriate service
5. Service interacts with repositories and returns DTOs
6. Controller returns either a Thymeleaf view or a JSON response

---

## Folder Structure

```
healthapp/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/com/example/healthapp/
        │   ├── HealthAppApplication.java
        │   ├── config/
        │   │   ├── AppConfig.java
        │   │   ├── DataInitializer.java
        │   │   └── SecurityConfig.java
        │   ├── controller/
        │   │   ├── AiController.java
        │   │   ├── AppointmentController.java
        │   │   ├── AuthController.java
        │   │   ├── DoctorController.java
        │   │   ├── DoctorDashboardController.java
        │   │   ├── EyeTestController.java
        │   │   ├── HomeController.java
        │   │   ├── PaymentController.java
        │   │   ├── ServiceProviderController.java
        │   │   ├── ServiceProviderDashboardController.java
        │   │   ├── TestController.java
        │   │   ├── UserDashboardController.java
        │   │   └── VideoCallController.java
        │   ├── dto/
        │   │   ├── AiAnalysisResponse.java
        │   │   ├── ApiResponse.java
        │   │   ├── AppointmentDTO.java
        │   │   ├── AppointmentRequest.java
        │   │   ├── AuthResponse.java
        │   │   ├── DoctorDTO.java
        │   │   ├── DoctorProfileRequest.java
        │   │   ├── LoginRequest.java
        │   │   ├── MedicineDTO.java
        │   │   ├── PaymentRequest.java
        │   │   ├── RegisterRequest.java
        │   │   ├── ServiceProviderDTO.java
        │   │   ├── ServiceProviderRequest.java
        │   │   ├── TestResultDTO.java
        │   │   └── UserDTO.java
        │   ├── entity/
        │   │   ├── Appointment.java
        │   │   ├── Consultation.java
        │   │   ├── Doctor.java
        │   │   ├── Role.java
        │   │   ├── ServiceProvider.java
        │   │   ├── TestResult.java
        │   │   └── User.java
        │   ├── exception/
        │   │   ├── GlobalExceptionHandler.java
        │   │   ├── ResourceNotFoundException.java
        │   │   ├── UnauthorizedException.java
        │   │   └── UserAlreadyExistsException.java
        │   ├── repository/
        │   │   ├── AppointmentRepository.java
        │   │   ├── ConsultationRepository.java
        │   │   ├── DoctorRepository.java
        │   │   ├── ServiceProviderRepository.java
        │   │   ├── TestResultRepository.java
        │   │   └── UserRepository.java
        │   ├── security/
        │   │   ├── JwtAuthFilter.java
        │   │   ├── JwtUtil.java
        │   │   └── UserDetailsServiceImpl.java
        │   └── service/
        │       ├── AiService.java
        │       ├── AppointmentService.java
        │       ├── AuthService.java
        │       ├── DoctorService.java
        │       ├── PaymentService.java
        │       ├── ServiceProviderService.java
        │       ├── TestResultService.java
        │       ├── UserService.java
        │       └── impl/
        │           ├── AiServiceImpl.java
        │           ├── AppointmentServiceImpl.java
        │           ├── AuthServiceImpl.java
        │           ├── DoctorServiceImpl.java
        │           ├── PaymentServiceImpl.java
        │           ├── ServiceProviderServiceImpl.java
        │           ├── TestResultServiceImpl.java
        │           └── UserServiceImpl.java
        └── resources/
            ├── application.properties
            └── templates/
                ├── home.html
                ├── mental-health.html
                ├── auth/
                │   ├── login.html
                │   └── register.html
                ├── doctor/
                │   └── dashboard.html
                ├── serviceprovider/
                │   └── dashboard.html
                ├── shared/
                │   └── layout.html
                ├── user/
                │   └── dashboard.html
                └── services/
                    ├── book-appointment.html
                    ├── eye-checkup.html
                    ├── medicine-finder.html
                    ├── mental-health-test.html
                    ├── online-consultation.html
                    ├── report-analyzer.html
                    └── video-call.html
```

---

## Database Design

### Entities and Relationships

**User**
- `id`, `fullName`, `email`, `password` (BCrypt), `phone`, `role` (enum), `createdAt`, `updatedAt`
- Central identity entity; all other entities link back to `User`

**Doctor** — `@OneToOne` with `User`
- `specialization`, `qualification`, `hospital`, `location`, `bio`, `profileImageUrl`
- `doctorType` (ONLINE / OFFLINE), `isAvailable`, `consultationFee`, `experienceYears`
- `@OneToMany` → `Appointment`, `@OneToMany` → `Consultation`

**ServiceProvider** — `@OneToOne` with `User`
- `shopName`, `address`, `city`, `state`, `pincode`, `phone`, `email`
- `availableDays`, `openTime`, `closeTime`
- `availableDoctors`, `servicesOffered`, `pricingInfo`

**Appointment** — `@ManyToOne` with `User` and `Doctor`
- `appointmentDate`, `reason`, `notes`, `status`
- `stripePaymentIntentId`, `meetingRoomId`
- `appointmentType` (IN_PERSON / ONLINE)

**Consultation** — `@ManyToOne` with `User` and `Doctor`, `@OneToOne` with `Appointment`
- `roomId` (used by ZegoCloud), `status` (PENDING / ACCEPTED / REJECTED / COMPLETED)
- `scheduledAt`, `notes`

**TestResult** — `@ManyToOne` with `User`
- `testType` (EYE_CHECKUP / MENTAL_HEALTH), `resultSummary`, `recommendation`
- `score`, `createdAt`

---

## API Overview

### Authentication — `/api/auth`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register a new user | Public |
| POST | `/api/auth/login` | Login and receive JWT token | Public |

### Appointments — `/api/appointments`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/appointments` | Create an appointment | USER |
| GET | `/api/appointments/my` | Get current user's appointments | USER |
| GET | `/api/appointments/doctor` | Get appointments for logged-in doctor | DOCTOR |

### Doctors — `/api/doctor`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/doctor/all` | List all available doctors | Public |
| GET | `/api/doctor/offline` | List offline/clinic doctors | Public |
| GET | `/api/doctor/online` | List online doctors | Public |
| POST | `/api/doctor/profile` | Create/update doctor profile | DOCTOR |
| GET | `/api/doctor/my-profile` | Get own doctor profile | DOCTOR |

### Test Results — `/api/tests` and `/api/eye-test`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/tests/eye-checkup` | Submit full eye test result | Public |
| POST | `/api/tests/mental-health` | Submit mental health test result | Public |
| GET | `/api/tests/my` | Get all test results for current user | USER |
| POST | `/api/eye-test/visual` | Submit visual acuity sub-test | Public |
| POST | `/api/eye-test/color` | Submit color blindness sub-test | Public |
| POST | `/api/eye-test/amsler` | Submit Amsler grid sub-test | Public |

### AI Services — `/api/ai`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/ai/analyze-report` | Analyze a medical report (fixed prompt) | USER |
| POST | `/api/ai/suggest-medicines` | Suggest affordable alternatives (fixed prompt) | USER |

### Payment — `/api/payment`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/payment/create-intent` | Create Stripe payment intent | USER |
| POST | `/api/payment/confirm` | Confirm payment and book appointment | USER |

### Service Provider — `/api/provider`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/provider/profile` | Create/update provider profile | SERVICE_PROVIDER |
| GET | `/api/provider/my` | Get own provider profile | SERVICE_PROVIDER |
| GET | `/api/provider/all` | List all providers | Public |

### Page Routes (MVC)

| Route | Description |
|-------|-------------|
| `/` | Landing page |
| `/auth/login` | Login page |
| `/auth/register` | Registration page |
| `/dashboard/user` | User dashboard |
| `/dashboard/doctor` | Doctor dashboard |
| `/dashboard/provider` | Service provider dashboard |
| `/services/eye-checkup` | Eye checkup service |
| `/services/mental-health-test` | Mental health questionnaire |
| `/services/report-analyzer` | Medical report upload and analysis |
| `/services/medicine-finder` | Low-cost medicine finder |
| `/services/book-appointment` | Appointment booking with Stripe |
| `/services/online-consultation` | Online doctor consultation request |
| `/services/video-call` | ZegoCloud video call room |
| `/mental-health` | Mental Health & Manifestation awareness page |

---

## Authentication Flow

```
1. User submits credentials → POST /api/auth/login
2. AuthService validates email + BCrypt password
3. JwtUtil generates a signed JWT (HS256, configurable expiry)
4. Token returned in AuthResponse { token, role, name }
5. Frontend stores token in localStorage
6. All subsequent API calls include: Authorization: Bearer <token>
7. JwtAuthFilter intercepts every request:
   a. Extracts token from Authorization header
   b. Validates signature and expiry via JwtUtil
   c. Loads UserDetails from UserDetailsServiceImpl
   d. Sets Authentication in SecurityContextHolder
8. SecurityConfig enforces role-based access on all routes
9. Controllers use @PreAuthorize or SecurityContext for identity
```

Token storage on the client uses `localStorage`. Role (`USER`, `DOCTOR`, `SERVICE_PROVIDER`) is stored alongside the token and used to redirect to the appropriate dashboard after login.

---

## AI Usage

Two AI-powered endpoints exist in the application. Both use **strictly fixed, hardcoded prompts** — user input is treated as data only, never as instruction.

### Report Analyzer
```
Fixed prompt: "You are a medical assistant. Analyze the following medical report 
and provide a simplified, plain-language explanation suitable for a non-medical 
person. Do not provide diagnosis or treatment advice."
```

### Medicine Finder
```
Fixed prompt: "You are a pharmacist assistant. Given this prescription or medicine 
name, suggest 3 cheaper generic alternatives available in India. Return a JSON 
array only..."
```

**Safety measures:**
- No free-form prompt construction — user input cannot alter the instruction
- AI responses are parsed and validated before display
- Markdown fences stripped; fallback bracket-search applied on malformed JSON
- Spring AI auto-configurations (`OpenAiAutoConfiguration`, `ChatClientAutoConfiguration`) are excluded to prevent unintended bean injection

---

## Payment Integration

Stripe is integrated for appointment booking via the payment intent flow.

**Flow:**
1. User selects a doctor and submits the booking form
2. Frontend calls `POST /api/payment/create-intent` with amount and currency
3. Stripe returns a `clientSecret` for the PaymentIntent
4. Frontend uses Stripe.js to collect card details and confirm the payment
5. On success, `POST /api/payment/confirm` is called
6. `AppointmentService` creates the appointment and stores the `paymentIntentId`

**Note:** The Stripe keys configured in this project are placeholder/test keys. No real transactions are processed. Replace `STRIPE_PUBLIC_KEY` and `STRIPE_SECRET_KEY` with live keys only in a production-secured environment.

---

## Video Call Integration

ZegoCloud is integrated as the video call infrastructure for online consultations.

**Flow:**
1. Patient submits a consultation request via `/services/online-consultation`
2. A `Consultation` record is created with status `PENDING`
3. Doctor views the request on their dashboard and accepts it
4. On acceptance: a `roomId` is generated and saved to the `Consultation` entity
5. Both patient and doctor are redirected to `/services/video-call?roomId=<id>&role=<role>`
6. `video-call.html` reads `roomId` from a `<meta>` tag injected by the server
7. ZegoCloud SDK initializes the room using `ZEGO_APP_ID` and `ZEGO_SERVER_SECRET`
8. ZegoCloud owns the full camera and microphone lifecycle — no browser `getUserMedia` probe

**Note:** ZegoCloud credentials are placeholders. Replace with real credentials from the ZegoCloud console for production deployment.

---

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL 14+
- A Stripe account (test mode)
- An OpenAI API key
- A ZegoCloud project (AppID + ServerSecret)

### 1. Clone the Repository

```bash
git clone https://github.com/AkashGitX/healthapp.git
cd healthapp
```

### 2. Configure the Database

Create a PostgreSQL database:

```sql
CREATE DATABASE healthapp;
```

### 3. Set Environment Variables

Set the following environment variables before running:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/healthapp
export OPENAI_API_KEY=your_openai_key
export STRIPE_PUBLIC_KEY=pk_test_your_key
export STRIPE_SECRET_KEY=sk_test_your_key
export ZEGO_APP_ID=your_zego_app_id
export ZEGO_SERVER_SECRET=your_zego_server_secret
export SESSION_SECRET=your_session_secret
```

### 4. Build and Run

```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080`.

### 5. Verify Startup

Check the console for:

```
========================================
  APP STARTED SUCCESSFULLY
  HealthApp is ready to serve requests.
========================================
```

### 6. Seed Data

`DataInitializer` automatically seeds demo accounts on first run:

| Role | Email | Password |
|------|-------|----------|
| USER | `user@healthapp.com` | `User@123` |
| DOCTOR (offline) | `doctor1@healthapp.com` | `Doctor@123` |
| DOCTOR (online) | `doctor7@healthapp.com` | `Doctor@123` |
| SERVICE_PROVIDER | `store@healthapp.com` | `Store@123` |

---

## Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `DATABASE_URL` | PostgreSQL JDBC connection string | Yes |
| `OPENAI_API_KEY` | OpenAI API key for AI features | Yes |
| `STRIPE_PUBLIC_KEY` | Stripe publishable key | Yes |
| `STRIPE_SECRET_KEY` | Stripe secret key | Yes |
| `ZEGO_APP_ID` | ZegoCloud application ID | Yes |
| `ZEGO_SERVER_SECRET` | ZegoCloud server secret | Yes |
| `SESSION_SECRET` | Application session signing secret | Yes |
| `PORT` | Server port (defaults to 8080) | No |

> Never commit real values for any of these variables. Use `.env` files locally and a secrets manager in production.

---

## Screenshots

> _Screenshots to be added after UI finalization._

| Screen | Description |
|--------|-------------|
| `screenshots/home.png` | Landing page with all service cards |
| `screenshots/login.png` | JWT login form |
| `screenshots/user-dashboard.png` | User dashboard with appointments |
| `screenshots/doctor-dashboard.png` | Doctor dashboard with consultation requests |
| `screenshots/eye-checkup.png` | Three-stage eye test UI |
| `screenshots/medicine-finder.png` | AI medicine suggestion cards |
| `screenshots/book-appointment.png` | Stripe-integrated appointment booking |
| `screenshots/video-call.png` | ZegoCloud video consultation room |

---

## Future Improvements

- Email notifications for appointment confirmation and consultation acceptance
- Real-time doctor availability updates via WebSocket
- PDF generation for test result reports
- Patient medical history timeline view
- Admin dashboard for platform management
- Push notifications for mobile browsers (PWA)
- Rate limiting on AI endpoints to control OpenAI costs
- Audit logging for all sensitive actions
- Docker Compose setup for local development
- CI/CD pipeline with GitHub Actions

---

## Contributing

Contributions are welcome. Please follow these steps:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes with a clear message: `git commit -m "feat: add your feature"`
4. Push to your fork: `git push origin feature/your-feature-name`
5. Open a pull request against `main` with a description of the changes

Please ensure:
- No API keys or secrets are included in the commit
- Code follows the existing package structure
- All new endpoints are secured appropriately in `SecurityConfig`

---


---

## Author

**Akash Sutradhar**

- LinkedIn: [akash-sutradhar-b6a305287](https://www.linkedin.com/in/akash-sutradhar-b6a305287/)
- GitHub: [AkashGitX](https://github.com/AkashGitX)
- Email: [aec.csbs.akashsutradhar@gmail.com](mailto:aec.csbs.akashsutradhar@gmail.com)

---

> Built with Java 17 · Spring Boot 3 · PostgreSQL · Thymeleaf · Stripe · ZegoCloud · OpenAI

package com.akash.medicalproj.config;

import com.akash.medicalproj.entity.Doctor;
import com.akash.medicalproj.entity.Role;
import com.akash.medicalproj.entity.ServiceProvider;
import com.akash.medicalproj.entity.User;
import com.akash.medicalproj.repository.DoctorRepository;
import com.akash.medicalproj.repository.ServiceProviderRepository;
import com.akash.medicalproj.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds the database with demo doctors and medical stores on first startup.
 * Runs only when the tables are completely empty — never overwrites real data.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository           userRepository;
    private final DoctorRepository         doctorRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final PasswordEncoder          passwordEncoder;

    @Override
    public void run(String... args) {
        seedDoctors();
        seedServiceProviders();
        backfillDoctorTypes();
    }


    private void backfillDoctorTypes() {
        doctorRepository.findAll().forEach(d -> {
            boolean changed = false;
            if (d.getDoctorType() == null) {
                d.setDoctorType(Doctor.DoctorType.ONLINE);
                changed = true;
                log.info("Backfilled doctorType=ONLINE for doctor id={}", d.getId());
            }
            // Demo clinic doctors were accidentally seeded as ONLINE — correct them
            if (d.getUser() != null
                    && d.getUser().getEmail() != null
                    && d.getUser().getEmail().endsWith("@healthapp.demo")
                    && d.getDoctorType() != Doctor.DoctorType.OFFLINE) {
                d.setDoctorType(Doctor.DoctorType.OFFLINE);
                changed = true;
                log.info("Migrated demo doctor id={} to OFFLINE", d.getId());
            }
            if (changed) doctorRepository.save(d);
        });
    }

    // ── Doctors ────────────────────────────────────────────────
    private void seedDoctors() {
        if (doctorRepository.count() > 0) {
            log.info("Doctors already seeded — skipping.");
            return;
        }

        log.info("Seeding demo doctors...");

        Object[][] doctorData = {
            // { email, fullName, phone, specialization, qualification, hospital, location, fee, experience, bio }
            {
                "dr.amit.sharma@healthapp.demo",
                "Amit Sharma",
                "+91 98100 11001",
                "Cardiologist",
                "MBBS, MD (Cardiology)",
                "Apollo Heart Institute",
                "New Delhi",
                500,
                12,
                "Expert cardiologist with 12 years of experience in treating heart diseases, hypertension, and cardiac rehabilitation."
            },
            {
                "dr.priya.singh@healthapp.demo",
                "Priya Singh",
                "+91 98200 22002",
                "Dermatologist",
                "MBBS, MD (Dermatology)",
                "Skin & Hair Care Centre",
                "Mumbai",
                400,
                8,
                "Specialist in skin disorders, cosmetic dermatology, hair loss, and allergy treatments."
            },
            {
                "dr.rahul.verma@healthapp.demo",
                "Rahul Verma",
                "+91 98300 33003",
                "General Physician",
                "MBBS, DTCD",
                "City Health Clinic",
                "Bangalore",
                300,
                10,
                "General physician experienced in managing fevers, infections, diabetes, and preventive healthcare."
            },
            {
                "dr.sneha.gupta@healthapp.demo",
                "Sneha Gupta",
                "+91 98400 44004",
                "Neurologist",
                "MBBS, DM (Neurology)",
                "BrainCare Hospital",
                "Hyderabad",
                700,
                15,
                "Senior neurologist specializing in migraine, epilepsy, stroke, and neurodegenerative conditions."
            },
            {
                "dr.ankit.roy@healthapp.demo",
                "Ankit Roy",
                "+91 98500 55005",
                "Orthopedic Surgeon",
                "MBBS, MS (Orthopaedics)",
                "Bone & Joint Hospital",
                "Kolkata",
                450,
                9,
                "Orthopedic surgeon skilled in joint replacement, sports injuries, spine issues, and fracture care."
            },
            {
                "dr.meena.krishnan@healthapp.demo",
                "Meena Krishnan",
                "+91 98600 66006",
                "Pediatrician",
                "MBBS, MD (Paediatrics)",
                "Children's Wellness Hospital",
                "Chennai",
                350,
                11,
                "Dedicated pediatrician providing expert care for newborns, infants, and children up to age 18."
            }
        };

        for (Object[] d : doctorData) {
            try {
                User user = User.builder()
                        .email((String) d[0])
                        .fullName((String) d[1])
                        .phone((String) d[2])
                        .password(passwordEncoder.encode("Doctor@123"))
                        .role(Role.DOCTOR)
                        .build();
                user = userRepository.save(user);

                Doctor doctor = Doctor.builder()
                        .user(user)
                        .specialization((String) d[3])
                        .qualification((String) d[4])
                        .hospital((String) d[5])
                        .location((String) d[6])
                        .consultationFee(BigDecimal.valueOf((int) d[7]))
                        .experienceYears((int) d[8])
                        .bio((String) d[9])
                        .isAvailable(true)
                        .doctorType(Doctor.DoctorType.OFFLINE)
                        .build();
                doctorRepository.save(doctor);

                log.info("Seeded doctor: Dr. {}", d[1]);
            } catch (Exception e) {
                log.warn("Could not seed doctor {}: {}", d[1], e.getMessage());
            }
        }

        log.info("Doctor seeding complete. {} doctors available.", doctorRepository.count());
    }

    // ── Service Providers (Medical Stores) ─────────────────────
    private void seedServiceProviders() {
        if (serviceProviderRepository.count() > 0) {
            log.info("Service providers already seeded — skipping.");
            return;
        }

        log.info("Seeding demo medical stores...");

        Object[][] storeData = {
            // { email, fullName, shopName, address, city, state, phone, days, open, close, doctors, services, pricing }
            {
                "healthcare.pharmacy@healthapp.demo",
                "HealthCare Pharmacy",
                "HealthCare Pharmacy",
                "12, City Center Mall, MG Road",
                "Bangalore",
                "Karnataka",
                "+91 80 4100 1100",
                "Mon–Sat",
                "09:00",
                "21:00",
                "Dr. Rahul Verma (General Physician), Dr. Priya Singh (Dermatologist)",
                "Pharmacy, Blood Tests, BP Check, Glucose Test, ECG",
                "Blood Test: ₹150, BP Check: ₹30, Glucose Test: ₹50, ECG: ₹200"
            },
            {
                "medplus.store@healthapp.demo",
                "MedPlus Store",
                "MedPlus Store",
                "45, Main Road, Sector 18",
                "Noida",
                "Uttar Pradesh",
                "+91 120 4200 2200",
                "Mon–Sun",
                "08:00",
                "22:00",
                "Dr. Amit Sharma (Cardiologist), Dr. Rahul Verma (General Physician)",
                "Pharmacy, X-Ray, Pathology, Nursing Services, Vaccination",
                "X-Ray: ₹300, Full Blood Count: ₹200, Vaccination: ₹500, Nursing Visit: ₹250"
            },
            {
                "apollo.pharmacy@healthapp.demo",
                "Apollo Pharmacy",
                "Apollo Pharmacy",
                "8, Near Apollo Hospital, Jubilee Hills",
                "Hyderabad",
                "Telangana",
                "+91 40 6600 3300",
                "Mon–Sun",
                "00:00",
                "23:59",
                "Dr. Sneha Gupta (Neurologist), Dr. Meena Krishnan (Pediatrician)",
                "24/7 Pharmacy, Lab Tests, Home Sample Collection, ICU Supplies",
                "Home Sample: ₹100, Lipid Profile: ₹350, Thyroid Test: ₹400, HbA1c: ₹300"
            },
            {
                "lifecare.medicals@healthapp.demo",
                "LifeCare Medicals",
                "LifeCare Medicals",
                "22, Market Area, Anna Nagar",
                "Chennai",
                "Tamil Nadu",
                "+91 44 5500 4400",
                "Mon–Sat",
                "08:30",
                "20:30",
                "Dr. Ankit Roy (Orthopedic), Dr. Meena Krishnan (Pediatrician)",
                "Pharmacy, Physiotherapy, Medical Equipment Rental, Diet Consultation",
                "Physiotherapy: ₹400/session, Wheelchair Rental: ₹500/week, Diet Plan: ₹300"
            }
        };

        for (Object[] s : storeData) {
            try {
                User user = User.builder()
                        .email((String) s[0])
                        .fullName((String) s[1])
                        .phone((String) s[6])
                        .password(passwordEncoder.encode("Store@123"))
                        .role(Role.SERVICE_PROVIDER)
                        .build();
                user = userRepository.save(user);

                ServiceProvider provider = ServiceProvider.builder()
                        .user(user)
                        .shopName((String) s[2])
                        .address((String) s[3])
                        .city((String) s[4])
                        .state((String) s[5])
                        .phone((String) s[6])
                        .availableDays((String) s[7])
                        .openTime((String) s[8])
                        .closeTime((String) s[9])
                        .availableDoctors((String) s[10])
                        .servicesOffered((String) s[11])
                        .pricingInfo((String) s[12])
                        .isActive(true)
                        .build();
                serviceProviderRepository.save(provider);

                log.info("Seeded medical store: {}", s[2]);
            } catch (Exception e) {
                log.warn("Could not seed provider {}: {}", s[2], e.getMessage());
            }
        }

        log.info("Service provider seeding complete. {} stores available.", serviceProviderRepository.count());
    }
}

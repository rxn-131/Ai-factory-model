package com.example.data

import com.example.model.HackathonTheme
import com.example.model.TechStack

data class MarkdownPreset(
    val id: String,
    val title: String,
    val problemId: String,
    val theme: HackathonTheme,
    val recommendedStack: TechStack,
    val summary: String,
    val markdown: String
)

object SampleMarkdownPresets {
    val presets = listOf(
        MarkdownPreset(
            id = "sih_agritech_drone",
            title = "KisanDrishti: Autonomous Crop Disease & Edge AI Drone",
            problemId = "SIH-1428",
            theme = HackathonTheme.AGRITECH,
            recommendedStack = TechStack.ANDROID_COMPOSE,
            summary = "On-device leaf blight detection with localized vernacular guidance, soil telemetry, and automated fertilizer calculation.",
            markdown = """
# KisanDrishti: Autonomous Crop Disease AI & Smart Farm Telemetry
**Problem ID:** SIH-1428 | **Theme:** Agriculture & Rural Development | **Category:** Software & Edge AI

## 1. Executive Problem Statement
Indian smallholder farmers lose over 35% of crop yields annually due to delayed pest/blight detection and overuse of expensive chemical fertilizers. Rural connectivity is intermittent (2G/3G in farm fields), making cloud-only AI solutions impractical. Farmers require an offline-first mobile app that runs on-device vision models, correlates sensor telemetry (soil moisture, pH, temperature), and calculates precise micronutrient dosages in vernacular Indian languages.

## 2. Core Functional Requirements
- **FR-1 [Offline Vision Diagnosis]:** Real-time camera leaf scan for paddy, cotton, wheat, and tomato blights with confidence score.
- **FR-2 [Telemetry Ingestion]:** Bluetooth/BLE sync with low-cost ESP32 soil probe measuring NPK, moisture, and temperature.
- **FR-3 [Vernacular Advisory Engine]:** Generates step-by-step organic/chemical remediation in Hindi, Telugu, Tamil, Marathi, Punjabi, and Bengali.
- **FR-4 [Govt Subsidy & Mandi Integration]:** Real-time MSP (Minimum Support Price) rates and direct PM-Kisan subsidy claim guidance.
- **FR-5 [Geo-Tagged Outbreak Radar]:** Community disease spread heatmaps synced peer-to-peer via local Wi-Fi direct/mesh.

## 3. Data Entities & Schema
- `FarmerProfile`: id, name, aadhaarHash, village, cropType, acreage, languagePreference
- `ScanRecord`: id, timestamp, gpsLat, gpsLng, cropName, diseaseDetected, severityScore, leafImageUrl
- `TelemetryReading`: id, probeMacAddress, nitrogen, phosphorus, potassium, moisturePct, soilTemp
- `RemediationPlan`: id, diseaseId, organicAlternative, chemicalDosageGramPerAcre, sprayWindowHours

## 4. Non-Functional & Hackathon Constraints
- Sub-500ms inference latency on mid-range Android devices.
- Zero internet dependency for critical crop saving decisions.
- Storage budget: SQLite/Room database with under 35MB overall package footprint.
- High accessibility: Voice narration for low-literacy farmers.
            """.trimIndent()
        ),
        MarkdownPreset(
            id = "sih_disaster_mesh",
            title = "AapdaNet: Offline Mesh Triage & Disaster Evacuation",
            problemId = "SIH-1290",
            theme = HackathonTheme.DISASTER_MGMT,
            recommendedStack = TechStack.ANDROID_COMPOSE,
            summary = "P2P Bluetooth/Wi-Fi Direct mesh network for emergency SOS, survivor triage, and NDRF relief allocation during telecom blackouts.",
            markdown = """
# AapdaNet: Resilient Offline Mesh Network for Disaster Evacuation
**Problem ID:** SIH-1290 | **Theme:** Disaster Management | **Category:** Mobile & Embedded Mesh

## 1. Executive Problem Statement
During severe cyclonic storms and flash floods (e.g., Odisha, Kerala, Assam), cellular cell towers collapse within 4 hours. Survivors and local first responders have smartphones with functioning radios (Wi-Fi, Bluetooth, GPS) but no internet uplink. NDRF and civil defense forces struggle to locate trapped victims and prioritize medical supplies.

## 2. Core Functional Requirements
- **FR-1 [Zero-Carrier P2P Mesh]:** Ad-hoc mesh formation via Wi-Fi Direct and BLE advertising packet relay up to 10 hops.
- **FR-2 [Emergency SOS Broadcast]:** 1-tap SOS transmitting victim GPS coordinates, blood group, battery level, and family count.
- **FR-3 [Triage Classification System]:** Red (Critical/Trauma), Yellow (Urgent), Green (Stable), Black (Deceased) victim labeling.
- **FR-4 [Relief Resource Ledger]:** Decentralized tracking of drinking water packets, ORS, blankets, and boats per relief camp.
- **FR-5 [Sync Gateway]:** When an NDRF drone or vehicle with satellite internet enters range, auto-dumps cached mesh packets to state disaster headquarters.

## 3. Data Entities & Schema
- `MeshPacket`: packetId, senderHash, hopCount, ttl, payloadType, timestamp, signature
- `VictimBeacon`: beaconId, victimName, age, triageColor, latitude, longitude, trappedUnderRubble, batteryPct
- `ReliefCamp`: campId, locationName, capacity, currentOccupancy, waterLitersLeft, medicalDoctorPresent
- `ReliefRequest`: requestId, campId, supplyCategory, quantityNeeded, priorityRank

## 4. Hackathon Competitive Edge
- Works with zero sim card or active telecom carrier.
- Cryptographic hop-validation prevents hoax emergency signals.
- Ultra-low battery consumption mode (sleep cycling radios).
            """.trimIndent()
        ),
        MarkdownPreset(
            id = "sih_healthcare_triage",
            title = "SanjeevaniAI: Rural PHC Tele-Triage & Prescription OCR",
            problemId = "SIH-1510",
            theme = HackathonTheme.HEALTHCARE,
            recommendedStack = TechStack.FASTAPI_AI,
            summary = "AI-assisted clinical triage, handwritten prescription digitizer, and drug interaction checker for tier-3 health centers.",
            markdown = """
# SanjeevaniAI: Primary Health Center Clinical Decision Support
**Problem ID:** SIH-1510 | **Theme:** Healthcare & Biomedical | **Category:** Fullstack AI & Backend

## 1. Problem Statement
Rural Primary Health Centers (PHCs) in India operate with a 1:11,000 doctor-to-patient ratio. ASHA workers and community nurses must triage dozens of emergency patients daily, decipher doctor handwriting, and prevent fatal drug contraindications without immediate specialist access.

## 2. Core Functional Requirements
- **FR-1 [Vitals AI Risk Scoring]:** Calculates pediatric/adult qSOFA sepsis index, hypertension crisis, and diabetic ketoacidosis risk.
- **FR-2 [Handwritten Rx OCR]:** Converts messy cursive doctor prescriptions into standard FHIR/ABDM compliant digital records.
- **FR-3 [Drug Interaction Matrix]:** Real-time warning for lethal contraindications (e.g., Warfarin + Aspirin, Metformin + Contrast).
- **FR-4 [Ayushman Bharat Sync]:** Integrates ABDM (Ayushman Bharat Digital Mission) Health ID and ABHA creation.
- **FR-5 [Vernacular Voice Intake]:** Patient symptom questionnaire in regional languages converted to structured clinical notes.

## 3. Architecture & API Endpoints
- `POST /api/v1/triage/assess`: Ingests vitals, returns risk tier (Low, Moderate, Emergency) and triage action checklist.
- `POST /api/v1/prescriptions/digitize`: Multi-modal OCR endpoint parsing handwritten medication names, dosages, frequencies.
- `POST /api/v1/drugs/contraindications`: Evaluates medication array against clinical safety knowledge graph.
- `GET /api/v1/patients/{abhaId}/timeline`: Retrieves longitudinal patient medical history.
            """.trimIndent()
        ),
        MarkdownPreset(
            id = "sih_smart_mobility",
            title = "Vithika: Edge CV Dynamic Green Corridor & Traffic Flow",
            problemId = "SIH-1335",
            theme = HackathonTheme.SMART_CITIES,
            recommendedStack = TechStack.REACT_TAILWIND,
            summary = "Real-time edge computer vision traffic signal optimization with automated ambulance green corridor clearance.",
            markdown = """
# Vithika: Edge CV Adaptive Traffic Management & Emergency Corridor
**Problem ID:** SIH-1335 | **Theme:** Smart Mobility & Cities | **Category:** Web Dashboard & Edge Analytics

## 1. Problem Statement
Urban Indian junctions waste millions of liters of fuel daily due to static pre-timed traffic timers. Furthermore, ambulances carrying critical organ transplant or cardiac patients are blocked in bottleneck congestion, causing avoidable fatalities.

## 2. Functional Requirements
- **FR-1 [YOLO Vehicle Density Counting]:** Edge camera feeds calculate real-time queue length (PCU - Passenger Car Units) per lane.
- **FR-2 [Dynamic Timer Algorithm]:** Dynamic green light phase allocation adjusting in real-time according to queue pressure.
- **FR-3 [Ambulance VIP Corridor]:** Siren acoustic detection + GPS transponder tracking automatically flips downstream signals green 800m ahead.
- **FR-4 [City Command Dashboard]:** Live junction telemetry, carbon emission savings, average wait time metrics, and manual override.
- **FR-5 [Citizen Congestion Feed]:** Public open-data feed reporting live intersection delays and alternate route advisories.
            """.trimIndent()
        ),
        MarkdownPreset(
            id = "sih_cyber_defense",
            title = "KavachUPI: Deepfake Voice & Fraudulent Gateway Interceptor",
            problemId = "SIH-1604",
            theme = HackathonTheme.CYBER_SECURITY,
            recommendedStack = TechStack.NEXTJS_FULLSTACK,
            summary = "Real-time AI behavioral anomaly detection, fake QR code scanner, and voice-clone synthesis detector for digital banking.",
            markdown = """
# KavachUPI: Autonomous AI Defense against Financial Social Engineering
**Problem ID:** SIH-1604 | **Theme:** Cyber Security & Defense | **Category:** Fullstack Security & Next.js

## 1. Problem Statement
UPI transactions in India exceed 13 billion monthly. Cybercriminals employ deepfake voice calls impersonating family members, malicious QR codes redirecting to spoofed payment gateways, and reverse-charge fraud targeting senior citizens and rural users.

## 2. Core Functional Requirements
- **FR-1 [Synthetic Voice / Deepfake Shield]:** In-call audio spectral analysis flagging synthesized neural TTS or voice cloning.
- **FR-2 [Malicious QR URL Quarantine]:** Decodes and sandboxes dynamic UPI Intent URIs to verify VPA ownership before PIN entry.
- **FR-3 [Behavioral Biometrics Guard]:** Monitors anomalous keystroke dynamics, touchscreen velocity, and orientation panic indicators.
- **FR-4 [National Cyber Crime Reporting]:** 1-click auto-generation of Indian Cyber Crime Portal (1930) complaint payload with hash evidence.
            """.trimIndent()
        ),
        MarkdownPreset(
            id = "sih_smart_edtech",
            title = "VidyaSetu: Multi-Lingual STEM AR & Vernacular AI Tutor",
            problemId = "SIH-1780",
            theme = HackathonTheme.SMART_EDUCATION,
            recommendedStack = TechStack.FLUTTER_DART,
            summary = "Gamified, offline-first science & math lab simulator with speech-driven multilingual conversational tutoring.",
            markdown = """
# VidyaSetu: Vernacular AI STEM Laboratory for Rural Schools
**Problem ID:** SIH-1780 | **Theme:** Smart Vernacular Education | **Category:** Cross-Platform Mobile

## 1. Problem Statement
Over 65% of rural government schools lack physical physics, chemistry, and biology laboratories. Students learn abstract formulas by rote memorization. There is a lack of high-quality vernacular STEM learning material aligned with state and NCERT curricula.

## 2. Core Functional Requirements
- **FR-1 [Interactive Physics & Chemistry Engine]:** Virtual optics bench, titration simulation, and circuit sandbox running on budget tablets.
- **FR-2 [Vernacular Voice Socratic AI]:** Conversational voice tutor explaining concepts in native tongue with cultural analogies.
- **FR-3 [Gamified Progress Quests]:** Micro-challenges with badge rewards, peer competition, and teacher diagnostic dashboard.
- **FR-4 [Offline Sync via Peer Bluetooth]:** Students can swap lab assignments and submit answers to the teacher's phone without internet.
            """.trimIndent()
        )
    )
}

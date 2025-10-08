# 🐉 ThaiTales

ThaiTales is an Android mobile application that introduces users to fascinating **Thai Mythical Creatures** through interactive descriptions and AI-generated short stories.  
Built using **Kotlin**, **Supabase**, and **Gemini AI**, the app provides a seamless blend of culture, storytelling, and technology.
---
# Screens Layout

**Intro Screen and Home Screen**

<img width="401" height="893" alt="Screenshot 2025-10-08 221033" src="https://github.com/user-attachments/assets/8af18726-3e22-4c31-8e0f-f60bc388f3af" />
<img width="401" height="893" alt="image" src="https://github.com/user-attachments/assets/2231b89b-3411-4108-93e6-5438d9955dcc" />




**Creature Description Screen and Story Generation**

<img width="400" height="894" alt="image" src="https://github.com/user-attachments/assets/caf80ab3-3e5a-4c30-ad43-61f1ea9da03b" />
<img width="402" height="887" alt="image" src="https://github.com/user-attachments/assets/64f87be1-b39a-43ff-a07e-257c4a85a5fc" />


**Favorites Screen**
Creatures and Stories

<img width="402" height="896" alt="image" src="https://github.com/user-attachments/assets/fa30c2d6-4adf-45a6-a62c-ae6c71f534ee" />
<img width="401" height="897" alt="image" src="https://github.com/user-attachments/assets/eb2d44c3-8dcb-45af-ae8d-9fc650f52399" />

---

## ✨ Features

- 🧠 **AI Story Generation** — Generate short stories inspired by Thai mythical creatures using **Gemini AI**.
- 📱 **Multi-Screen Navigation** — Explore details of each creature in a dedicated screen.
- 💾 **Data Persistence** — Save favorite creatures locally with **SharedPreferences**.
- ☁️ **Cloud Connectivity** — Sync data with **Supabase** for creature data and favorite storage.
- ⚡ **Modern UI** — Smooth transitions and responsive layouts for an enjoyable experience.

---

## 🧩 Tech Stack

| Component | Technology |
|------------|-------------|
| Programming Language | Kotlin |
| IDE | Android Studio |
| Backend | Supabase (PostgreSQL + REST API) |
| AI Integration | Google Gemini API |
| Local Storage | SharedPreferences |
| Architecture | MVVM pattern (ViewModel, Repository) |

---
**Note**
Edit and add your AI Model's API key in your local.properties. In our case, we used Gemini 2.5 Flash API.

---
## ⚙️ How It Works

1. The app **fetches creature data** (names, descriptions, and images) from **Supabase**.
2. Users can **browse** and **select a creature** to view detailed information.
3. From the detail page, users can:
   - ⭐ **Save** a creature as a favorite (synced both locally and in Supabase).
   - ✍️ **Generate a short AI story** about that creature using Gemini AI.
4. The **favorites** page loads data from both local storage and Supabase for a synced experience.

---

## 🗃️ Data Model

| Field | Description |
|--------|-------------|
| `id` | Unique identifier for each creature |
| `name` | Name of the creature |
| `description` | Short description of the creature |
| `image_url` | Image link of the creature |


# FitPlanner Pro

Kompletna aplikacja fitness na Androida (Kotlin + Jetpack Compose + MVVM + Room + DataStore).

## Funkcje
- Dashboard z podsumowaniem dnia i postępem tygodniowym.
- Plany treningowe (CRUD + oznaczanie wykonania + historia treningów).
- Dieta (CRUD posiłków, oznaczanie spożycia, podsumowanie makro).
- Atlas ćwiczeń (wyszukiwarka, filtry, CRUD ćwiczeń własnych).
- Profil użytkownika (edycja, BMI, statystyki, tryb ciemny).
- Dolna nawigacja: Start / Plany / Dieta / Atlas / Profil.

## Architektura
- `data/` — Room (encje, DAO, baza), DataStore, repozytoria.
- `domain/` — modele domenowe.
- `ui/` — ekrany Compose, komponenty, motyw, ViewModel.
- `navigation/` — definicje tras nawigacji.
- `utils/` — konwertery Room i stałe.

## Uruchomienie
1. Otwórz folder projektu w Android Studio Iguana lub nowszym.
2. Poczekaj na synchronizację Gradle.
3. Uruchom konfigurację `app` na emulatorze lub urządzeniu.

## Wymagania
- JDK 17
- Android SDK 34

## Dane startowe
Baza Room jest prepopulowana przykładowym profilem, planem treningowym, posiłkiem, ćwiczeniem i wpisem historii treningowej.

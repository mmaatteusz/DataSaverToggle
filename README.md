# Data Saver Toggle – Samsung / Android

Prosta aplikacja i widget do szybkiego otwierania systemowej zakładki **Oszczędzanie danych**.

## Jak działa
Android nie pozwala zwykłej aplikacji bezpośrednio przełączać globalnego Data Saver bez specjalnych uprawnień systemowych. Ta wersja nie używa Accessibility, roota ani Shizuku.

Widget na ekranie głównym jednym tapnięciem otwiera bezpośrednio systemową zakładkę **Oszczędzanie danych**, gdzie przełączasz opcję ręcznie.

## Użycie
1. Zainstaluj APK.
2. Przytrzymaj pusty fragment ekranu głównego.
3. Wybierz **Widgety**.
4. Znajdź **Data Saver**.
5. Dodaj widget.
6. Kliknięcie widgetu otwiera ustawienia Oszczędzania danych.

## GitHub Actions
Workflow `.github/workflows/build-apk.yml` buduje instalowalny APK.
Artefakt nazywa się `DataSaverToggle-APK` i zawiera `DataSaverToggle.apk`.

Rebuild trigger: 2026-09-19.

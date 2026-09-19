# Data Saver Toggle – Samsung / Android

Mała aplikacja do szybkiego włączania i wyłączania systemowego **Oszczędzania danych**.

## Dlaczego potrzebuje Ułatwień dostępu?
Android nie pozwala zwykłej aplikacji bezpośrednio zmieniać globalnego Data Saver (`MANAGE_NETWORK_POLICY` jest uprawnieniem systemowym). Ta wersja nie wymaga roota ani Shizuku: po Twoim naciśnięciu otwiera systemowy ekran Data Saver, naciska jego główny przełącznik i wraca do aplikacji.

Usługa jest ograniczona w konfiguracji do pakietu `com.android.settings` i reaguje tylko, gdy w aplikacji został wydany rozkaz WŁĄCZ/WYŁĄCZ.

## Pierwsze uruchomienie
1. Zainstaluj apkę.
2. Otwórz ją i naciśnij **Włącz / sprawdź usługę dostępu**.
3. W „Zainstalowane aplikacje” / „Zainstalowane usługi” wybierz **Przełączanie oszczędzania danych** i zezwól.
4. Wróć do aplikacji.
5. Od teraz używaj **WŁĄCZ** i **WYŁĄCZ**.

## GitHub Actions — automatyczne APK
Workflow `.github/workflows/build-apk.yml` buduje instalowalny debug APK na GitHubie.
Po udanym uruchomieniu artefakt nazywa się `DataSaverToggle-APK` i zawiera `DataSaverToggle.apk`.

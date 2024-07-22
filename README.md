[![Build and Upload Artifacts](https://github.com/dinesh-thiyagarajan/Juice-Vendor/actions/workflows/build.yml/badge.svg)](https://github.com/dinesh-thiyagarajan/Juice-Vendor/actions/workflows/build.yml)

# Juice Vendor

**Juice Vendor** is a Kotlin Multiplatform project targeting Android, Desktop for displaying the juice order placed via [Juice Kadai](https://github.com/dinesh-thiyagarajan/Juice-Kadai)

# Screenshots

<p align="center">
  <img src="https://github.com/user-attachments/assets/632a0dfa-f3f0-415a-9ef0-cbca154edbf6" width="250" alt="Juice Vendor Landing Screen"/>
  <img src="https://github.com/user-attachments/assets/7838a035-52cd-4af1-82cf-98ca44b78441" width="250" alt="Juice Vendor Report Screen"/>
  <img src="https://github.com/user-attachments/assets/4387ee52-b4a2-4ada-8c73-59f77f9a6c79" width="250" alt="Juice Vendor Add New Juice Screen"/>
</p>

# Setup Instructions

- Install Android Studio (Latest Stable or Canary Build)
- Add Kotlin Multi platform Plugin
- Please read the documentation for compose multiplatform [setup](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-setup.html) and follow the process
- Choose the type of build that you wanted, ex: desktop or android
- Sync the gradle and see if you are able to build the project, if successful click on run
- Create a new project in firebase
- Copy the PROJECT_ID, APP_ID, API_KEY, FIREBASE_DB_URL from firebase console and paste it in local.properties in their respective fields
- Add your Service Account email under the key SERVICE_ACCOUNT_ID and add PRINT_HTTP_LOGS="true" in local.properties along with the above given values
- Run this command ./gradlew generateBuildKonfig in the terminal in the root directory of the project, inside composeApp/build/buildkonfig a BuildKonfig file will be generated, any keys and values referenced in the local.properties can be accessed from here
- Run the app

# Product Spec
- The app listens to the [Juice Kadai](https://github.com/dinesh-thiyagarajan/Juice-Kadai) app to check and display new orders automatically
- Reports for the current day can be seen in the report section
- New Juices can be added from the Add new Juice screen
- Juices availability can be updated from the same screen
- Add/Remove Admin operations can be done by either an Admin or from a Service Account 

# Acknowledgements

- The icons used in this app are from [SVG Repo](https://www.svgrepo.com)
  

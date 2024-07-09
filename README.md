# Juice Vendor

**Juice Vendor** is a Kotlin Multiplatform project targeting Android, Desktop for displaying the juice order placed via [Juice Kadai](https://github.com/dinesh-thiyagarajan/Juice-Kadai)

# Screenshots

<p align="center">
  <img src="https://github.com/dinesh-thiyagarajan/Juice-Vendor/assets/17405840/dd698747-1003-4340-99e5-b99c2891f2d7" width="250" alt="Juice Vendor Landing Screen"/>
  <img src="https://github.com/dinesh-thiyagarajan/Juice-Vendor/assets/17405840/72e6f40e-3a08-4027-ae1a-5e88fa9d8bf5" width="250" alt="Juice Vendor Report Screen"/>
  <img src="https://github.com/dinesh-thiyagarajan/Juice-Vendor/assets/17405840/8841457d-05d4-4deb-85f9-13f166030142" width="250" alt="Juice Vendor Add New Juice Screen"/>
</p>

# Setup Instructions

- Install Android Studio (Latest Stable or Canary Build)
- Add Kotlin Multi platform Plugin
- Please read the documentation for compose multiplatform [setup](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-setup.html) and follow the process
- Choose the type of build that you wanted, ex: desktop, android, ios
- Sync the gradle and see if you are able to build the project, if successful click on run
- Create a new project in firebase
- Copy the FIREBASE_DATABASE_URL and FIREBASE_API_KEY from firebase console and paste it in local.properties in their respective fields
- Run this command ./gradlew generateBuildKonfig in the terminal in the root directory of the project, inside composeApp/build/buildkonfig a BuildKonfig file will be generated, any keys and values referenced in the local.properties can be accessed from here
- Run the app

# Acknowledgements

- The icons used in this app are from [SVG Repo](https://www.svgrepo.com)
  

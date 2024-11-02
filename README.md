# compose-table
Table component for Kotlin Multiplatform

`@Composable` table widget

- Automatic layout
- Composable cells
- Grid lines
- Spanning rows and columns
- Easy to use

Gradle configuration for Kotlin Multiplatform:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.github.zakgof:table:1.0.0")

```

Quick start

```kotlin
Table {
    Row {
        Text(text = "1")
        Text(text = "John Doe")
        Text(text = "johndoe@somemail.com")
    }
    Row {
        Text(text = "2")
        Text(text = "Jane Doe")
        Text(text = "janedoe@somemail.com")
    }
}
```
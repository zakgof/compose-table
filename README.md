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
Table (lineWidth = 2.dp, lineColor = Color.BLUE) {
    Row {
        Text(text = "Cell 1")
        Text(text = "Cell 2")
    }
    Row {
        Text(text = "Long cell", modifier = Modifier.columnSpan(2))
    }
}
```
# JFX Frameless

Modular window shell toolkit for JavaFX. Compose custom undecorated windows from building blocks: resize behavior, platform-native title bars, flexible layout slots.

<div align="center">

<table>
<tr>
<td align="center">

![Showcase](https://github.com/user-attachments/assets/6b0f34fd-0571-4cac-ac4a-e631f0e0a4c0)
<br>
<em>JFX-Frameless makes stunning UIs like this possible!</em>

</td>
</tr>
</table>

</div>

## What it does

- Transparent, undecorated windows with optional rounded corners
- Platform-native controls: macOS traffic lights, Windows 11 Fluent buttons
- Edge-based resizing in all directions
- Composable layout: title bar, sidebars, content, footer
- JavaFX as `provided` - you bring your own runtime

## Requirements

- Java 21+
- JavaFX 21+

## Installation

**Maven:**
```xml
<dependency>
    <groupId>de.bsommerfeld</groupId>
    <artifactId>jfx-frameless</artifactId>
    <version>0.1</version>
</dependency>
```

**Gradle:**
```gradle
implementation 'de.bsommerfeld:jfx-frameless:0.1'
```

JavaFX is `provided`. Add `javafx-controls` (Maven: `org.openjfx:javafx-controls:21`, Gradle: same).

## Usage

### Minimal

```java
new WindowShell(primaryStage)
    .withDefaultTitleBar()
    .withResizable()
    .withMinSize(400, 300)
    .withContent(new Label("Hello"))
    .show();
```

### Builder

```java
WindowShell window = WindowShellBuilder.create(stage)
    .defaultTitleBar()
    .resizable()
    .minSize(400, 300)
    .size(1200, 800)
    .cornerRadius(16)
    .content(myContent)
    .leftSidebar(navigation)
    .stylesheet(getClass().getResource("/css/app.css").toExternalForm())
    .build();

window.show();
```

### Custom Title Bar

```java
TitleBar titleBar = new TitleBar(stage)
    .withPlatformControls()
    .bindIcon()                     // binds to stage.getIcons()
    .bindTitle()                    // binds to stage.titleProperty()
    .withRightContent(settingsButton, themeToggle);

// Or with static values:
TitleBar titleBar = new TitleBar(stage)
    .withPlatformControls()
    .withIcon(myLogoNode)           // static node, null-safe
    .withTitle("My App");           // static string

new WindowShell(stage)
    .withTitleBar(titleBar)
    .withResizable()
    .withContent(myContent)
    .show();
```

### Bare minimum

Skip features you don't need:

```java
// No title bar, no resize, square corners
new WindowShell(stage)
    .withSquareCorners()
    .withContent(myContent)
    .show();
```

## Layout Structure

```
StackPane (root, rounded clip)
  └─ BorderPane (layout)
       ├─ top: TitleBar
       ├─ left: sidebar
       ├─ center: StackPane (content)
       ├─ right: sidebar
       └─ bottom: footer
```

## Components

| Class | Purpose |
|-------|---------|
| `WindowShell` | Stage wrapper with fluent API |
| `WindowShellBuilder` | Builder pattern alternative |
| `TitleBar` | Draggable bar with left/center/right slots |
| `MacOSControls` | Traffic light buttons |
| `WindowsControls` | Fluent Design buttons |
| `ResizeBehavior` | Edge detection + drag resize |
| `OperatingSystem` | Runtime platform detection |

## Styling

Minimal default stylesheet at `/css/frameless.css` (optional):

```java
shell.withStylesheet(getClass().getResource("/css/frameless.css").toExternalForm());
```

The default CSS provides only structural essentials (dimensions, layout).
Colors and theming are left to your application's stylesheet.

CSS classes:

| Class | Element |
|-------|---------|
| `.window-shell-root` | Root StackPane |
| `.window-shell-layout` | BorderPane layout |
| `.window-shell-content` | Content pane |
| `.title-bar` | Title bar |
| `.title-bar-title` | Title label |
| `.title-bar-icon` | Icon node |
| `.macos-controls` | macOS button group |
| `.traffic-light-button` | macOS button |
| `.windows-controls` | Windows button group |
| `.window-control-button` | Windows button |
| `.close-button` | Close button |

## JPMS

```java
module your.app {
    requires de.bsommerfeld.jfx.frameless;
    requires javafx.controls;
}
```

## License

MIT

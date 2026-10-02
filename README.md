# AppUni

Aplicación Android que muestra, con realidad aumentada, los convenios de una universidad sobre un **mapa mundial impreso**.

La persona apunta la cámara a esa hoja. Cuando el teléfono reconoce el mapa, aparecen marcadores en las ciudades de las universidades socias. Al tocar un marcador se leen el nombre, la ciudad, el país, el logo, el tipo de convenio, la descripción y el sitio web.

El mapa, los marcadores y las fichas funcionan sin Internet. Abrir un sitio web sí necesita conexión. Si el teléfono no puede ejecutar la experiencia, la aplicación permanece abierta y lo explica. No usa códigos QR ni alineación manual.

Los datos del catálogo son de muestra (`https://example.com`). No son convenios reales.

## Dónde está la programación en Java

El lenguaje de la aplicación es **Java 17**. Los archivos `*.kts` son scripts de Gradle, no pantallas ni reglas de negocio.

| Qué buscar | Ruta |
| --- | --- |
| Pantallas, permisos y apertura del sitio web | `app/src/main/java/appuni/explore/ui/` |
| Reglas del catálogo, fichas, cámara y marcadores | `domain/src/main/java/appuni/explore/domain/` |
| Latitud y longitud convertidas a un punto del mapa | `coordinates/src/main/java/appuni/explore/coordinates/` |
| Lectura del JSON local | `data/src/main/java/appuni/explore/data/` |
| ARCore, SceneView, anclas y seguimiento del mapa | `ar/src/main/java/appuni/explore/ar/` |
| Pruebas JUnit | `domain/src/test/java/`, `coordinates/src/test/java/`, `data/src/test/java/` |

La actividad de arranque es `appuni.explore.ui.MainActivity`. El paquete de la aplicación es `appuni.explore`.

`MapArView` vive en el módulo `ar` y entrega a las pantallas una vista normal. Las pantallas no importan ARCore ni SceneView.

## Qué contiene cada carpeta

| Carpeta | Contenido |
| --- | --- |
| `app` | Módulo Android de la aplicación: layouts, catálogo, mapa de referencia y código de interfaz. |
| `domain` | Módulo Java puro. Modelos y reglas. No depende de Android ni de la cámara. |
| `coordinates` | Módulo Java puro. Convierte una ciudad en metros sobre la imagen. Se prueba sin teléfono. |
| `data` | Biblioteca Android. Lee `partners.json` y `world-map.json` con `org.json`. |
| `ar` | Biblioteca Android. Único módulo que usa ARCore y SceneView. |
| `gradle` | Versiones de las bibliotecas y el wrapper de Gradle 8.11.1. |
| `specs/001-explore-agreements` | Especificación, plan, tareas, contratos y criterios de la función. |
| `.specify` | Constitución del proyecto y scripts de Spec Kit 1.0.12. |
| `docs` | Tutorial en Word: `Tutorial_SDD_AppUni_Android_Studio_Java.docx`. |
| `.cursor/skills` | Instrucciones del flujo Spec Kit para el asistente de desarrollo. |

Dentro de `app/src/main`:

| Ruta | Contenido |
| --- | --- |
| `java/appuni/explore/ui/` | `MainActivity`, `ExploreViewModel`, `WebsiteOpener`. |
| `res/layout/` | Pantallas de comprobación, mapa y ficha del convenio. |
| `assets/catalog/partners.json` | Universidad anfitriona y socias de muestra. |
| `assets/catalog/logos/` | Logos de las socias que tienen imagen. |
| `assets/maps/world-map.png` | Imagen que debe reconocer la cámara. |
| `assets/maps/world-map.json` | Ancho impreso, proyección y límites geográficos. El ancho de referencia es 0,40 m. |

Los módulos se declaran en `settings.gradle.kts`: `app`, `domain`, `coordinates`, `data` y `ar`.

`app` depende de `domain`, `data` y `ar`. `data` depende de `domain`. `ar` depende de `domain` y `coordinates`. `coordinates` no depende de ningún otro módulo.

## Cómo abrirlo

1. Instala Android Studio y un JDK 17.
2. En Android Studio elige **Open** y selecciona esta carpeta raíz, la que contiene `settings.gradle.kts`. No abras solo `app`.
3. Espera a que Gradle sincronice. Android Studio creará `local.properties` con la ruta de tu SDK. Ese archivo no se sube a GitHub.
4. El código Java está en `src/main/java` de cada módulo.
5. Conecta un teléfono con depuración USB. Para ver marcadores hace falta Google Play Services for AR y el mapa impreso a 40 cm de ancho.

Datos de compilación, tomados de los archivos Gradle:

- Java 17
- `minSdk` 24 (Android 7.0)
- `compileSdk` y `targetSdk` 36
- Android Gradle Plugin 8.9.2
- SceneView `arsceneview` 2.2.1, solo en `ar`
- Material Components 1.12.0 para las pantallas

ARCore es opcional en el manifiesto. Sin él se puede instalar la aplicación y ver la explicación de que el teléfono no puede ejecutar la experiencia. Hace falta para mostrar el mapa.

## Pruebas y APK

En PowerShell, desde esta carpeta:

```text
.\gradlew.bat :coordinates:test :domain:test :data:test
.\gradlew.bat :app:assembleDebug
```

En macOS o Linux:

```text
./gradlew :coordinates:test :domain:test :data:test
./gradlew :app:assembleDebug
```

El APK de depuración queda en `app/build/outputs/apk/debug/app-debug.apk`. Esa carpeta no está en GitHub. Hay que generarla en cada máquina.

Las pruebas automáticas cubren coordenadas, reglas del catálogo, separación de marcadores, estados de seguimiento y lectura del JSON. No encienden la cámara. No hay pruebas de instrumentación.

La última ejecución registrada de esas pruebas JVM fue de 34 ejecuciones, 0 fallos y 0 omitidas. Eso no demuestra que un teléfono haya reconocido el mapa impreso.

## Qué falta validar

En `specs/001-explore-agreements/tasks.md` siguen abiertas:

- **T024.** Puntuar `world-map.png` con la herramienta `arcoreimg`. La imagen está en el proyecto, pero no hay una puntuación guardada. El umbral pedido es 75 o más.
- **T049.** Recorrer en un teléfono los escenarios de `specs/001-explore-agreements/quickstart.md`: permiso de cámara, reconocimiento, ciudades, movimiento, ficha, universidades cercanas, Close y Atrás, pérdida del mapa, modo avión, sitio web y un teléfono sin capacidad de AR.

## Dónde leer el diseño

| Archivo | Qué responde |
| --- | --- |
| `.specify/memory/constitution.md` | Reglas duraderas: Android, módulos separados, pruebas de la lógica y fallos de cámara sin cerrar la aplicación. |
| `specs/001-explore-agreements/spec.md` | Qué debe hacer la aplicación. |
| `specs/001-explore-agreements/plan.md` | Cómo está construida. |
| `specs/001-explore-agreements/research.md` | Decisiones: mapa impreso, datos locales, Java, vistas y `org.json`. |
| `specs/001-explore-agreements/tasks.md` | Tareas hechas y las dos que siguen abiertas. |
| `docs/Tutorial_SDD_AppUni_Android_Studio_Java.docx` | Tutorial para quien no participó en el desarrollo. |

El comportamiento se especificó primero. Una implementación anterior usó Kotlin y Jetpack Compose. La aplicación actual es Java con vistas y Material Components. Los scripts `build.gradle.kts` se mantuvieron. Eso no significa que las pantallas sigan escritas en Kotlin.

## Descargar el ZIP y abrirlo en Eclipse

AppUni es una aplicación de teléfono. El código está en Java, pero no es un programa de escritorio. En Eclipse no aparece una ventana con el mapa del mundo. El botón **Run As → Java Application** no sirve: no hay un `main` que dibuje la aplicación en el computador.

Con Eclipse se puede leer el código, modificarlo y lanzar las pruebas de lógica. Para instalar la aplicación en un teléfono hace falta el Android SDK. La forma más simple de conseguir ese SDK es instalar Android Studio una vez, aunque después se edite el código en Eclipse.

### 1. Descargar

1. Abre https://github.com/jdVegaS24/AppUni
2. Pulsa el botón verde **Code**.
3. Pulsa **Download ZIP**.
4. El archivo descargado se llama `AppUni-main.zip`. Suele quedar en la carpeta Descargas.

### 2. Descomprimir

1. Entra en Descargas.
2. Haz clic derecho sobre `AppUni-main.zip`.
3. Elige **Extraer todo**.
4. Pulsa **Extraer**.
5. Se crea una carpeta llamada `AppUni-main`. Ábrela.
6. Dentro deben verse, entre otras, estas cosas: `app`, `domain`, `ar`, `settings.gradle.kts`, `gradlew.bat` y este `README.md`. Si solo ves otra carpeta, entra en ella hasta encontrar `settings.gradle.kts`. Esa es la carpeta del proyecto. No abras solo `app`.

### 3. Instalar Java 17

1. Si ya tienes Java 17, sáltalo. En PowerShell, `java -version` debe decir versión 17.
2. Si no, instala un JDK 17, por ejemplo Temurin 17, desde https://adoptium.net/
3. Vuelve a abrir PowerShell y comprueba `java -version`.

Eclipse tiene que usar ese JDK. En Eclipse: **Window → Preferences → Java → Installed JREs → Add… → Standard VM** y elige la carpeta del JDK 17. Márcalo como predeterminado.

### 4. Importar el proyecto en Eclipse

Hace falta Eclipse IDE for Java Developers, no un editor de texto. La importación usa Gradle, que ya viene en la carpeta (`gradlew.bat`). No hace falta instalar Gradle aparte.

1. Abre Eclipse.
2. **File → Import…**
3. Abre la carpeta **Gradle**.
4. Elige **Existing Gradle Project**. Pulsa **Next**.
5. En **Project root directory** pulsa **Browse…** y elige la carpeta descomprimida `AppUni-main`, la que contiene `settings.gradle.kts`.
6. Pulsa **Finish**.
7. Espera. La primera vez Gradle descarga bibliotecas. Puede tardar varios minutos y pide Internet.
8. Si Eclipse pide confiar en el proyecto de Gradle, acepta.

Al terminar deben aparecer varios proyectos: `app`, `domain`, `coordinates`, `data` y `ar`. El código Java de cada uno está en `src/main/java`.

Si la importación Gradle no aparece en el menú, instala en Eclipse el componente Buildship: **Help → Eclipse Marketplace…**, busca **Buildship Gradle Integration** e instálalo. Reinicia Eclipse y repite la importación.

### 5. Ver el código

| Quieres ver | En Eclipse abre |
| --- | --- |
| La pantalla principal | `app` → `src/main/java` → `appuni.explore.ui` → `MainActivity.java` |
| Las reglas | `domain` → `src/main/java` → `appuni.explore.domain` |
| El cálculo del mapa | `coordinates` → `src/main/java` → `appuni.explore.coordinates` → `GeoToImage.java` |
| La lectura del catálogo | `data` → `src/main/java` → `appuni.explore.data` → `CatalogParser.java` |
| La cámara y los marcadores | `ar` → `src/main/java` → `appuni.explore.ar` |

### 6. Ejecutar las pruebas que no necesitan teléfono

Estas pruebas comprueban números y reglas. No abren la cámara.

1. En el proyecto `coordinates` o `domain`, entra en `src/test/java`.
2. Haz clic derecho sobre una clase que termine en `Test`, por ejemplo `GeoToImageTest`.
3. Elige **Run As → JUnit Test**.
4. Abajo, la vista **JUnit** muestra una barra verde si pasó.

Si Eclipse dice que no encuentra JUnit, clic derecho sobre el proyecto Gradle → **Gradle → Refresh Gradle Project** y vuelve a intentarlo.

`CatalogParserTest`, dentro de `data`, sí necesita el SDK de Android. Si esa prueba falla porque no existe el SDK, no es un fallo de la lógica del catálogo: falta la herramienta de Android descrita en el paso siguiente.

### 7. Generar la aplicación para el teléfono

Eclipse no instala el Android SDK por sí solo. Sin ese SDK, `app`, `data` y `ar` no terminan de compilar.

1. Instala Android Studio desde https://developer.android.com/studio aunque vayas a seguir editando en Eclipse.
2. Ábrelo una vez y deja que instale el Android SDK. Puede ser la misma carpeta `AppUni-main`.
3. Cierra Android Studio si solo lo usaste para el SDK.
4. En la carpeta descomprimida, la primera compilación crea `local.properties` con la ruta del SDK. Si no existe, créalo con un editor de texto. Una línea, con tu ruta real:

```text
sdk.dir=C:\\Users\\TU_USUARIO\\AppData\\Local\\Android\\Sdk
```

5. En Eclipse puedes lanzar la construcción sin salir del IDE: clic derecho sobre el proyecto `app` → **Run As → Gradle Build…** → en **Gradle Tasks** escribe `assembleDebug` → **Run**. También vale abrir PowerShell en `AppUni-main` y ejecutar:

```text
.\gradlew.bat :app:assembleDebug
```

6. Si termina bien, el archivo instalable está en:

```text
AppUni-main\app\build\outputs\apk\debug\app-debug.apk
```

7. Copia ese archivo al teléfono, ábrelo e instálalo. Android pedirá permiso para instalar aplicaciones de este origen. El paquete se llama `appuni.explore`.
8. Para ver los marcadores, el teléfono necesita Google Play Services for AR y el mapa impreso a 40 cm de ancho. En el computador no se ve esa cámara.

### 8. Errores frecuentes

| Lo que pasa | Qué hacer |
| --- | --- |
| Eclipse abre una carpeta vacía o sin código | Elegiste una carpeta de más afuera o de más adentro. Vuelve a importar la que contiene `settings.gradle.kts`. |
| **Run As → Java Application** no hace nada o da error | Es normal. Esta aplicación no se ejecuta así. Usa las pruebas JUnit o genera el APK. |
| Gradle no descarga nada | Revisa el Internet y un proxy. La primera sincronización no funciona sin red. |
| No existe `local.properties` o dice que falta el SDK | Completa el paso 7. Eclipse no trae el SDK de Android. |
| El teléfono dice que la aplicación no puede ejecutar la experiencia del mapa | El teléfono no tiene soporte de AR. La aplicación debe quedarse abierta y explicarlo. No es el mismo fallo que un error de compilación. |

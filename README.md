Тестовое задания для Севстар.
Погодное приложение: Запрашивает права на GPS, отображает текущую погоду по текущим координатам. 
Отображается: город, регион, текущая/по ощущениям температура, скорость ветра, текущая погода и иконка погоды.
Автообновление погоды с запросом местоположения каждые 30 секунд.
Стек:
  1. Kotlin
  2. MVVM - использовалось вместо MVP
  3. Retrofit - для запроса погоды с интернтета.
  4. Coil - загрузка иконки с интернета.
  5. Coroutine - использовалось вместо RxJava2.
  6. Dagger 2.

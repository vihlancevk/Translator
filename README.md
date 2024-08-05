# Переводчик
Простое веб-приложение для перевода набора слов на другой язык с использованием стороннего сервиса перевода.
В этом приложении используется Yandex.Translate.
## Установка
Протестировано для `Ubuntu 22.04.4 LTS x86_64`
```
sudo apt update && sudo apt upgrade
sudo apt install -y git
sudo apt install -y openjdk-21-jdk
mkdir project
cd project/
git clone https://github.com/vihlancevk/Translator
cd Translator/
```
## Использование
1. [Получить api-ключ для использования Yandex.Translate](https://yandex.cloud/ru/docs/translate/operations/sa-api-key#create-account).
2. Открыть [файл](src/main/resources/application.yml) в корне проекта и заменить запись \<KEY> на свой ключ.
3. Запустить тесты командой `./gradlew test`, чтобы убедиться в работоспособности приложения с вашим ключом.
4. Запустить приложение командой `./gradlew run`, после чего открыть [браузер](http://localhost:8080/).
## Дополнительно
Для просмотры базы данных во время работы приложения необходимо открыть [страницу](http://localhost:8080/h2-console/).
Вся информация о настройке базы данных находится в [файле](src/main/resources/application.yml).

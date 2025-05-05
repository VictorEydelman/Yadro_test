# Yadro тестовое задание для Импульса

Выполнил: Эйдельман Виктор Аркадьевич
Вариант №3: Приложение для просмотра контактов с возможностью удалить повторяющиеся


## UserStory 1

В этой истории нужно было реализовать вывод всех контактов с краткой информацией о них.
![image](https://github.com/user-attachments/assets/deeeecac-aeae-491d-8104-27a4345d7eae)
Как можно видеть выводиться краткая информация (Имя, Фамилия, Номер телефона, email, фото) о всех контакт забитых в телефон.

Для сравнения можно посмотреть контакты телефона:
![image](https://github.com/user-attachments/assets/b92c2c99-5eb2-4134-91a1-af0203981d38)
Как мы видим список контактов совпадает.

### Код

Для отображения используется код на jetpack compose: [ContactScreen.kt](https://github.com/VictorEydelman/Yadro_test/blob/master/Yadro_test/app/src/main/java/mad/project/yadro_test/ContactScreen.kt)

Для получения списка контактов: [GetContact.kt](https://github.com/VictorEydelman/Yadro_test/blob/master/Yadro_test/app/src/main/java/mad/project/yadro_test/GetContact.kt)

## UserStory 2

В этой истории нужно было реализовать связь между клиентом (нашим приложением) и сервером по AIDL, на серевер удалить всех дублирующихся пользователей (одинаковое имя, фамилия, номер телефона, email, фото)
и затем вывести на основном приложение информацию о результате удаления и затем в приложение обновить список контактов.

Для демонстрации были созданы 3 одинаковых контакта:
![image](https://github.com/user-attachments/assets/e938e8b7-09f3-4ebf-ac4e-f4b74a5ff53b)
![image](https://github.com/user-attachments/assets/c19e6a08-fc8d-40a3-a7d5-f5a17cc2bfb8)

Давайте запустим удаление дубликатов
![image](https://github.com/user-attachments/assets/d63835b3-f16f-4ed0-a0c5-0092f7a2e6ad)
![image](https://github.com/user-attachments/assets/d4f33975-cdbb-4762-89fe-d0de58bf6d92)
Как мы видим дубликаты пропали

![image](https://github.com/user-attachments/assets/672a8ba5-7c30-4681-925d-40444bdbf9d1)
Сообщение пропало через 10 секунд

Теперь давайте запустим удаление дубликатов, когда таковых нету
![image](https://github.com/user-attachments/assets/42758258-6b9b-4f2a-9e38-568168d7d501)
Как мы видим, вывелась информация о отсутствие дубликатов

### Код

Для работы по AIDL есть интерфейс, который находится на клиенте и сервере:[IAidlInterface.aidl](https://github.com/VictorEydelman/Yadro_test/blob/master/AidlService/app/src/main/aidl/mad/project/aidlservice/IAidlInterface.aidl)

Для подключение к серверу есть класс: [AidlService.kt](https://github.com/VictorEydelman/Yadro_test/blob/master/AidlService/app/src/main/java/mad/project/aidlservice/AidlService.kt)

Для реализации IAidlInterface, есть класс: [AidlImpl.kt](https://github.com/VictorEydelman/Yadro_test/blob/master/AidlService/app/src/main/java/mad/project/aidlservice/AidlImpl.kt)

Для удаления дубликатов используется класс: [DeleteDuplicateContact.kt](https://github.com/VictorEydelman/Yadro_test/blob/master/AidlService/app/src/main/java/mad/project/aidlservice/DeleteDuplicateContact.kt)

Для отображения используется тот же код на jetpack compose: [ContactScreen.kt](https://github.com/VictorEydelman/Yadro_test/blob/master/Yadro_test/app/src/main/java/mad/project/yadro_test/ContactScreen.kt)

## Вывод:
В результате выполнения задания мне удалось написать Android приложение, которое позволяет видеть все контакты из телефона с краткой информацией о них и удалять автоматически дубликаты пользователей при этом удаляются, обновляя страницу.

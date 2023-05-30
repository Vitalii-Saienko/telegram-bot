Telegram Bot Project

This project includes a Telegram bot that provides information about job vacancies. The bot allows users to browse and select vacancies based on their experience level (junior, middle, senior). It retrieves vacancy data from a CSV file and displays the details of each vacancy to the users.
Project Structure

The project is organized into several packages:

    com.example.telegrambot.dto: Contains the VacancyDto class, which represents the data structure for a job vacancy.

    com.example.telegrambot.service: Contains the service classes responsible for reading vacancies from a file (VacanciesReaderService) and managing the vacancies (VacancyService).

    com.example.telegrambot: Contains the main application class (TelegramBotApplication), the Telegram bot registration class (BotRegister), and the Telegram bot implementation class (VacanciesBot).

VacancyDto Class

The VacancyDto class is a data transfer object (DTO) that represents a job vacancy. It contains the following fields:

    id: The unique identifier of the vacancy.
    title: The title of the vacancy.
    shortDescription: A short description of the vacancy.
    longDescription: A long description of the vacancy.
    company: The company offering the vacancy.
    salary: The salary for the vacancy.
    link: A link to more information about the vacancy.

VacanciesReaderService Class

The VacanciesReaderService class is responsible for reading vacancy data from a CSV file. It uses the OpenCSV library to parse the CSV file and convert its contents into a list of VacancyDto objects. The CSV file path is provided as a parameter to the getVacanciesFromFile method.
VacancyService Class

The VacancyService class manages the job vacancies. It initializes the vacancies by reading data from a CSV file during the application startup (@PostConstruct method). The VacanciesReaderService is used to read the vacancies from the file. The vacancies are stored in a map, where the vacancy ID is used as the key.

The class provides several methods to filter the vacancies based on the experience level: getJuniorVacancies, getMiddleVacancies, and getSeniorVacancies. These methods use Java streams and filters to select the vacancies that match the specified level. The get method retrieves a specific vacancy based on its ID.
BotRegister Class

The BotRegister class is responsible for registering the Telegram bot using the TelegramBotsApi. It uses the VacanciesBot class, which extends the TelegramLongPollingBot class and implements the bot's logic. The registration is performed during the application startup (@PostConstruct method).
VacanciesBot Class

The VacanciesBot class is the main implementation of the Telegram bot. It extends the TelegramLongPollingBot class and implements the onUpdateReceived method, which handles incoming updates from Telegram.

The class handles various types of updates, including messages and callback queries. It provides functionality for displaying the start menu, showing vacancies based on the user's selection, and displaying vacancy details. The class uses the VacancyService to retrieve the vacancy data.

The class also includes helper methods to generate reply keyboards for different vacancy levels and handle navigation within the bot.
TelegramBotApplication Class

The TelegramBotApplication class is the entry point of the application. It starts the Spring Boot application and registers the Telegram bot.
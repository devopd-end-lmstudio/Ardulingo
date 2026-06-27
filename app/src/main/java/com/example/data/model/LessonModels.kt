package com.example.data.model

enum class QuestionType {
    MULTIPLE_CHOICE,
    FILL_IN_BLANK,
    CODE_FIX,
    TAP_BLOCKS
}

data class Question(
    val id: String,
    val type: QuestionType,
    val instruction: String,
    val prompt: String,
    val options: List<String> = emptyList(),
    val correctAnswer: String = "",
    val correctOrder: List<String> = emptyList(),
    val explanation: String = ""
)

data class Lesson(
    val id: String,
    val title: String,
    val description: String,
    val moduleName: String,
    val questions: List<Question>,
    val xpReward: Int = 20,
    val difficulty: String = "Beginner"
)

object Curriculum {
    val MODULE_ARDUINO = "Arduino Basics"
    val MODULE_ESP32 = "ESP32 & IoT Basics"
    val MODULE_MORSE = "Morse Code Mastery"

    val lessons = listOf(
        // --- MODULE 1: ARDUINO BASICS ---
        Lesson(
            id = "ard_1",
            title = "Microcontrollers 101",
            description = "Learn what an Arduino is, how it processes code, and the physical parts of a board.",
            moduleName = MODULE_ARDUINO,
            difficulty = "Beginner",
            questions = listOf(
                Question(
                    id = "q_ard_1_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    instruction = "What is a Microcontroller?",
                    prompt = "Think of an Arduino as the brain of your project. What is a microcontroller essentially?",
                    options = listOf(
                        "A mini computer on a single integrated circuit chip",
                        "A standard desktop computer processor",
                        "A screen for displaying computer graphics",
                        "A simple cable that connects devices"
                    ),
                    correctAnswer = "0",
                    explanation = "A microcontroller is a compact integrated circuit designed to govern a specific operation in an embedded system. It includes a CPU, memory, and input/output peripherals."
                ),
                Question(
                    id = "q_ard_1_2",
                    type = QuestionType.FILL_IN_BLANK,
                    instruction = "Fill in the blank:",
                    prompt = "Arduino boards use physical pins to communicate with the world. To read a button press, you configure the pin as an ____.",
                    options = emptyList(),
                    correctAnswer = "INPUT",
                    explanation = "An INPUT pin reads electrical signals (like a button being pressed) from sensors or switches, while an OUTPUT pin sends signals (like turning on an LED)."
                ),
                Question(
                    id = "q_ard_1_3",
                    type = QuestionType.MULTIPLE_CHOICE,
                    instruction = "Identify the Pin Type",
                    prompt = "Which type of pin allows you to output simulated sliding levels (like LED dimming or speed control)?",
                    options = listOf(
                        "Digital Input Pin",
                        "PWM (Pulse Width Modulation) Pin",
                        "Power Supply Pin",
                        "Reset Pin"
                    ),
                    correctAnswer = "1",
                    explanation = "PWM (Pulse Width Modulation) pins can simulate analog outputs by pulsing digital signals on and off very rapidly, letting you dim LEDs or control motor speeds."
                )
            )
        ),
        Lesson(
            id = "ard_2",
            title = "The Setup & Loop",
            description = "Understand the skeleton of every Arduino script, the crucial setup() and loop() functions.",
            moduleName = MODULE_ARDUINO,
            difficulty = "Beginner",
            questions = listOf(
                Question(
                    id = "q_ard_2_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    instruction = "Skeleton Structure",
                    prompt = "Which function in an Arduino sketch runs exactly ONCE when the board power turns on or resets?",
                    options = listOf(
                        "loop()",
                        "setup()",
                        "main()",
                        "initBoard()"
                    ),
                    correctAnswer = "1",
                    explanation = "The setup() function runs once at startup. It is used to initialize pin modes, start serial communications, or set up libraries."
                ),
                Question(
                    id = "q_ard_2_2",
                    type = QuestionType.FILL_IN_BLANK,
                    instruction = "Code Completion",
                    prompt = "Complete this basic Arduino sketch template:\n\nvoid setup() {\n  // Init code\n}\n\nvoid ____() {\n  // Repeating code\n}",
                    options = emptyList(),
                    correctAnswer = "loop",
                    explanation = "The loop() function does exactly what its name says: it loops consecutively, allowing your program to change, respond, and control the board interactively."
                ),
                Question(
                    id = "q_ard_2_3",
                    type = QuestionType.CODE_FIX,
                    instruction = "Find the missing symbol",
                    prompt = "This setup code fails to compile because a character is missing. What should be added to fix it?\n\nvoid setup() {\n  pinMode(13, OUTPUT)\n}",
                    options = listOf(
                        "}",
                        ";",
                        "(",
                        "void"
                    ),
                    correctAnswer = "1",
                    explanation = "In C++ (which Arduino is based on), statements must be terminated with a semicolon (;). Adding a semicolon after OUTPUT) resolves the compilation error."
                )
            )
        ),
        Lesson(
            id = "ard_3",
            title = "Hello Blink!",
            description = "Learn how to write code to turn physical pins ON and OFF to blink an LED.",
            moduleName = MODULE_ARDUINO,
            difficulty = "Beginner",
            questions = listOf(
                Question(
                    id = "q_ard_3_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    instruction = "Set Pin Mode",
                    prompt = "How do you tell the Arduino that Pin 13 is hooked up to an LED and needs to output power?",
                    options = listOf(
                        "digitalWrite(13, HIGH);",
                        "pinMode(13, OUTPUT);",
                        "pinMode(13, INPUT);",
                        "analogWrite(13, 255);"
                    ),
                    correctAnswer = "1",
                    explanation = "You use pinMode(pin, mode) to configure a pin's behavior. To drive an LED, set it to OUTPUT."
                ),
                Question(
                    id = "q_ard_3_2",
                    type = QuestionType.FILL_IN_BLANK,
                    instruction = "Write the function name",
                    prompt = "To turn a digital pin fully ON (supplying 5V or 3.3V), you use:\n\n____Write(13, HIGH);",
                    options = emptyList(),
                    correctAnswer = "digital",
                    explanation = "The digitalWrite(pin, value) function writes a HIGH (ON) or LOW (OFF) value to a digital pin."
                ),
                Question(
                    id = "q_ard_3_3",
                    type = QuestionType.TAP_BLOCKS,
                    instruction = "Assemble the Blinking Loop",
                    prompt = "Arrange these code segments in the correct order to: 1. Turn LED on, 2. Wait 1 second, 3. Turn LED off, 4. Wait 1 second.",
                    options = listOf(
                        "digitalWrite(13, LOW);",
                        "delay(1000); // 2nd",
                        "digitalWrite(13, HIGH);",
                        "delay(1000); // 4th"
                    ),
                    correctOrder = listOf(
                        "digitalWrite(13, HIGH);",
                        "delay(1000); // 2nd",
                        "digitalWrite(13, LOW);",
                        "delay(1000); // 4th"
                    ),
                    explanation = "First you write HIGH to turn the LED on, delay for 1000 milliseconds, write LOW to turn it off, and delay again so it stays off before looping again!"
                )
            )
        ),
        Lesson(
            id = "ard_4",
            title = "Digital Inputs & Buttons",
            description = "Learn how to read physical buttons and handle internal pull-up configurations.",
            moduleName = MODULE_ARDUINO,
            difficulty = "Beginner",
            questions = listOf(
                Question(
                    id = "q_ard_4_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    instruction = "Reading Input States",
                    prompt = "Which function do you use to check whether a push button connected to Pin 7 is currently pressed?",
                    options = listOf(
                        "digitalRead(7)",
                        "digitalWrite(7, INPUT)",
                        "analogRead(7)",
                        "pinMode(7, INPUT)"
                    ),
                    correctAnswer = "0",
                    explanation = "The digitalRead(pin) function reads the electrical state of a physical digital pin, returning HIGH or LOW depending on the incoming voltage level."
                ),
                Question(
                    id = "q_ard_4_2",
                    type = QuestionType.FILL_IN_BLANK,
                    instruction = "Internal Resistors",
                    prompt = "To enable the board's internal pull-up resistor on digital Pin 7, you set:\npinMode(7, INPUT_____);",
                    options = emptyList(),
                    correctAnswer = "PULLUP",
                    explanation = "Using INPUT_PULLUP enables an internal resistor that pulls the pin state to HIGH when the button is open, preventing electrical noise."
                ),
                Question(
                    id = "q_ard_4_3",
                    type = QuestionType.CODE_FIX,
                    instruction = "Find the missing symbol",
                    prompt = "This loop turns on an LED when a button is pressed (LOW). Fix the comparison operator:\n\nvoid loop() {\n  if (digitalRead(btnPin) = LOW) {\n    digitalWrite(ledPin, HIGH);\n  }\n}",
                    options = listOf(
                        "==",
                        "===",
                        "!=",
                        "equals"
                    ),
                    correctAnswer = "0",
                    explanation = "In C++, a single '=' is for value assignment. To compare two values, you must use the double equal comparison operator '=='."
                )
            )
        ),
        Lesson(
            id = "ard_5",
            title = "Serial Communication",
            description = "Print logs and interact with your computer from the Arduino board using Serial.",
            moduleName = MODULE_ARDUINO,
            difficulty = "Intermediate",
            questions = listOf(
                Question(
                    id = "q_ard_5_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    instruction = "Configure Speed",
                    prompt = "What function must you call inside setup() to configure the transmission speed for sending messages to your computer?",
                    options = listOf(
                        "Serial.begin(9600);",
                        "Serial.print(9600);",
                        "Serial.open(9600);",
                        "Serial.connect(9600);"
                    ),
                    correctAnswer = "0",
                    explanation = "Serial.begin(baudrate) sets up the speed of communication (9600 baud is a standard default) between the board and your computer."
                ),
                Question(
                    id = "q_ard_5_2",
                    type = QuestionType.FILL_IN_BLANK,
                    instruction = "Printing Logs",
                    prompt = "To print a message to the Serial Monitor followed by an automatic new line break, you use:\n\nSerial.____(\"Processing data...\");",
                    options = emptyList(),
                    correctAnswer = "println",
                    explanation = "Serial.println() automatically appends a carriage return and newline character at the end, moving subsequent prints to the next line."
                )
            )
        ),

        // --- MODULE 2: ESP32 & IOT BASICS ---
        Lesson(
            id = "esp_1",
            title = "Meet the ESP32",
            description = "Explore why the ESP32 is the ultimate board for smart home, IoT, and high-performance projects.",
            moduleName = MODULE_ESP32,
            difficulty = "Intermediate",
            questions = listOf(
                Question(
                    id = "q_esp_1_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    instruction = "ESP32 Core Capabilities",
                    prompt = "What are the two major built-in wireless capabilities of an ESP32 chip that standard Arduinos lack?",
                    options = listOf(
                        "Wi-Fi and Bluetooth",
                        "NFC and GPS",
                        "Cellular 5G and FM Radio",
                        "Satellite Uplink and Infrared"
                    ),
                    correctAnswer = "0",
                    explanation = "The ESP32 is incredibly popular for Internet of Things (IoT) projects because it features fully integrated Wi-Fi and Bluetooth on a single, inexpensive microchip."
                ),
                Question(
                    id = "q_esp_1_2",
                    type = QuestionType.FILL_IN_BLANK,
                    instruction = "Voltage Levels",
                    prompt = "Unlike standard 5V Arduino Unos, ESP32 pins operate strictly at ____ volts. Connecting 5V directly to its pins can damage it!",
                    options = emptyList(),
                    correctAnswer = "3.3",
                    explanation = "ESP32 operates at a 3.3V logic level. Always double check your wiring and sensor voltage inputs before connecting them to ESP32 pins."
                )
            )
        ),
        Lesson(
            id = "esp_2",
            title = "Analog Inputs & Sensors",
            description = "Read continuous sliding values (like temperature or rotation) using Analog-to-Digital Converters (ADC).",
            moduleName = MODULE_ESP32,
            difficulty = "Intermediate",
            questions = listOf(
                Question(
                    id = "q_esp_2_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    instruction = "Analog Readings",
                    prompt = "Which built-in function is used to read a continuous voltage signal from an analog sensor (like a light sensor or potentiometer)?",
                    options = listOf(
                        "digitalRead(34)",
                        "analogRead(34)",
                        "analogWrite(34, 128)",
                        "readVoltage(34)"
                    ),
                    correctAnswer = "1",
                    explanation = "The analogRead(pin) function turns on the ADC (Analog to Digital Converter) on that pin to measure voltages between 0V and reference, returning an integer score."
                ),
                Question(
                    id = "q_esp_2_2",
                    type = QuestionType.FILL_IN_BLANK,
                    instruction = "ADC Resolution",
                    prompt = "The ESP32 ADC has 12-bit resolution by default, which means analogRead returns a range from 0 up to ____.",
                    options = emptyList(),
                    correctAnswer = "4095",
                    explanation = "A 12-bit binary integer can represent values from 0 to 4095 (2^12 = 4096 states), allowing for highly precise voltage sensing."
                )
            )
        ),
        Lesson(
            id = "esp_3",
            title = "Connecting to Wi-Fi",
            description = "Write ESP32 code to authenticate and connect to your home Wi-Fi network.",
            moduleName = MODULE_ESP32,
            difficulty = "Advanced",
            questions = listOf(
                Question(
                    id = "q_esp_3_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    instruction = "Wi-Fi Library",
                    prompt = "Which official library must you include at the top of your script to connect an ESP32 to a Wi-Fi network?",
                    options = listOf(
                        "#include <ESP32WiFi.h>",
                        "#include <WiFi.h>",
                        "#include <Network.h>",
                        "#include <InternetConnection.h>"
                    ),
                    correctAnswer = "1",
                    explanation = "On the ESP32 platform, the core library is named <WiFi.h>. You include it to access the WiFi singleton class."
                ),
                Question(
                    id = "q_esp_3_2",
                    type = QuestionType.TAP_BLOCKS,
                    instruction = "Assemble Connection Code",
                    prompt = "Arrange these statements in order to: 1. Start Wi-Fi, 2. Check connection state, 3. Print status.",
                    options = listOf(
                        "while (WiFi.status() != WL_CONNECTED) {",
                        "WiFi.begin(\"MySSID\", \"Password\");",
                        "  delay(500);",
                        "Serial.println(\"Connected!\");"
                    ),
                    correctOrder = listOf(
                        "WiFi.begin(\"MySSID\", \"Password\");",
                        "while (WiFi.status() != WL_CONNECTED) {",
                        "  delay(500);",
                        "Serial.println(\"Connected!\");"
                    ),
                    explanation = "First you initiate connection using WiFi.begin(ssid, pass), then you poll and wait in a while loop checking WiFi.status(), and finally print success!"
                )
            )
        ),
        Lesson(
            id = "esp_4",
            title = "ESP32 Touch Pins",
            description = "Leverage ESP32 capacitive touch capabilities to build touch-sensitive interfaces without physical switches.",
            moduleName = MODULE_ESP32,
            difficulty = "Intermediate",
            questions = listOf(
                Question(
                    id = "q_esp_4_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    instruction = "Capacitive Touch Reading",
                    prompt = "The ESP32 features built-in capacitive touch sensing. What function is used to read touch sensors on supported pins?",
                    options = listOf(
                        "touchRead(T0)",
                        "analogRead(T0)",
                        "digitalRead(T0)",
                        "getTouch(T0)"
                    ),
                    correctAnswer = "0",
                    explanation = "The touchRead(pin) function measures the capacitance of a metal plate connected to a touch-capable pin. This lets you build touch buttons without physical switches!"
                ),
                Question(
                    id = "q_esp_4_2",
                    type = QuestionType.FILL_IN_BLANK,
                    instruction = "Touch Input Dynamics",
                    prompt = "When you touch a touch-capable pin, the capacitive value returned by touchRead() goes ____ (higher or lower) due to human body capacitance.",
                    options = emptyList(),
                    correctAnswer = "lower",
                    explanation = "Touching the pin discharges some energy, causing the raw capacitive frequency/value returned by touchRead() to drop below your standard threshold."
                )
            )
        ),
        Lesson(
            id = "esp_5",
            title = "Deep Sleep & Power",
            description = "Put your ESP32 in deep sleep mode to run your smart IoT projects on battery for months or years.",
            moduleName = MODULE_ESP32,
            difficulty = "Advanced",
            questions = listOf(
                Question(
                    id = "q_esp_5_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    instruction = "Power Management",
                    prompt = "Which sleep mode puts the ESP32 in an ultra-low-power state, turning off CPU and Wi-Fi while preserving timer-wakeups?",
                    options = listOf(
                        "Deep Sleep Mode",
                        "Standby Mode",
                        "Off Mode",
                        "Idle Mode"
                    ),
                    correctAnswer = "0",
                    explanation = "Deep Sleep turns off almost everything except the RTC (Real-Time Clock) controller, bringing power usage down to micro-amps to preserve battery."
                ),
                Question(
                    id = "q_esp_5_2",
                    type = QuestionType.FILL_IN_BLANK,
                    instruction = "Sleep Wakeup Duration",
                    prompt = "To make your ESP32 wake up after 10 seconds of deep sleep, you call:\n\nesp_sleep_enable_timer_wakeup(10 * ____);",
                    options = emptyList(),
                    correctAnswer = "1000000",
                    explanation = "The ESP32 timer wakeup expects the delay duration in microseconds. Since 1 second = 1,000,000 microseconds, multiplying by 1,000,000 is required."
                )
            )
        ),

        // --- MODULE 3: MORSE CODE MASTERY ---
        Lesson(
            id = "mor_1",
            title = "Morse Code Basics",
            description = "Understand dots, dashes, and spacing that form the alphabet of radio communication.",
            moduleName = MODULE_MORSE,
            difficulty = "Beginner",
            questions = listOf(
                Question(
                    id = "q_mor_1_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    instruction = "The Time Unit",
                    prompt = "In Morse Code, a single Dash represents exactly how many Dots in duration?",
                    options = listOf(
                        "Two dots",
                        "Three dots",
                        "Four dots",
                        "Five dots"
                    ),
                    correctAnswer = "1",
                    explanation = "According to international standards, a dash is three times as long as a dot."
                ),
                Question(
                    id = "q_mor_1_2",
                    type = QuestionType.FILL_IN_BLANK,
                    instruction = "Identify Letter",
                    prompt = "In Morse Code, three consecutive Dots (· · ·) represent the letter ____.",
                    options = emptyList(),
                    correctAnswer = "S",
                    explanation = "Three dots (· · ·) stands for 'S', and three dashes (- - -) stands for 'O'. Together, S-O-S (· · · - - - · · ·) is the universal distress signal!"
                )
            )
        ),
        Lesson(
            id = "mor_2",
            title = "Arduino SOS Light",
            description = "Program your microcontroller pin to blink the SOS emergency signal in Morse Code.",
            moduleName = MODULE_MORSE,
            difficulty = "Intermediate",
            questions = listOf(
                Question(
                    id = "q_mor_2_1",
                    type = QuestionType.TAP_BLOCKS,
                    instruction = "Assemble S.O.S Letter S",
                    prompt = "Arrange these commands to create a single Dot 'S' signal (turn on, wait short dot duration 150ms, turn off, wait 150ms).",
                    options = listOf(
                        "delay(150); // wait off",
                        "digitalWrite(13, HIGH);",
                        "digitalWrite(13, LOW);",
                        "delay(150); // wait on"
                    ),
                    correctOrder = listOf(
                        "digitalWrite(13, HIGH);",
                        "delay(150); // wait on",
                        "digitalWrite(13, LOW);",
                        "delay(150); // wait off"
                    ),
                    explanation = "To signal a dot, you write HIGH, wait a brief duration (150ms), write LOW, and delay so there is spacing before the next mark."
                ),
                Question(
                    id = "q_mor_2_2",
                    type = QuestionType.FILL_IN_BLANK,
                    instruction = "Complete Code Block",
                    prompt = "To signal a Dash, you must leave the light ON for three times as long. If a Dot delay is 150ms, complete the Dash block:\n\ndigitalWrite(13, HIGH);\ndelay(____);\ndigitalWrite(13, LOW);",
                    options = emptyList(),
                    correctAnswer = "450",
                    explanation = "Since a dash is 3 times the duration of a dot (150ms * 3 = 450ms), a delay of 450 milliseconds is perfect for a dash."
                )
            )
        ),
        Lesson(
            id = "mor_3",
            title = "Morse Alphabet",
            description = "Master the core characters of the English alphabet in international Morse Code, from dots to dashes.",
            moduleName = MODULE_MORSE,
            difficulty = "Beginner",
            questions = listOf(
                Question(
                    id = "q_mor_3_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    instruction = "Letters A and B",
                    prompt = "In Morse Code, a single Dot followed immediately by a single Dash (· -) represents which letter of the alphabet?",
                    options = listOf(
                        "Letter A",
                        "Letter B",
                        "Letter M",
                        "Letter N"
                    ),
                    correctAnswer = "0",
                    explanation = "· - represents the letter 'A'. Conversely, - · · · represents the letter 'B'."
                ),
                Question(
                    id = "q_mor_3_2",
                    type = QuestionType.FILL_IN_BLANK,
                    instruction = "The Shortest Character",
                    prompt = "The most common letter in the English language has the shortest Morse signal: a single Dot (·). What letter is this?",
                    options = emptyList(),
                    correctAnswer = "E",
                    explanation = "A single dot (·) represents 'E', which is the shortest and easiest letter to transmit!"
                ),
                Question(
                    id = "q_mor_3_3",
                    type = QuestionType.MULTIPLE_CHOICE,
                    instruction = "Letter T",
                    prompt = "If a single Dot (·) is 'E', what letter is represented by a single Dash (-)?",
                    options = listOf(
                        "Letter T",
                        "Letter M",
                        "Letter O",
                        "Letter I"
                    ),
                    correctAnswer = "0",
                    explanation = "A single dash (-) represents 'T'. Together, E and T are the foundational single-mark characters in Morse code."
                )
            )
        ),
        Lesson(
            id = "mor_4",
            title = "Numbers & Distress",
            description = "Learn how numbers 1-5 are coded in 5-character groups, and decode distress signals.",
            moduleName = MODULE_MORSE,
            difficulty = "Intermediate",
            questions = listOf(
                Question(
                    id = "q_mor_4_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    instruction = "The Distress Code",
                    prompt = "The universal distress signal S-O-S is written in Morse Code as which sequence of marks?",
                    options = listOf(
                        "· · · - - - · · ·",
                        "- - - · · · - - -",
                        "· - · - · -",
                        "- - · · - -"
                    ),
                    correctAnswer = "0",
                    explanation = "SOS is composed of three dots (S), three dashes (O), and three dots (S) sent consecutively without word spaces."
                ),
                Question(
                    id = "q_mor_4_2",
                    type = QuestionType.FILL_IN_BLANK,
                    instruction = "Number 1 Sequence",
                    prompt = "All digits in Morse Code are composed of 5 elements. The number 1 is written as: · - - - - . How many dashes does it contain?",
                    options = emptyList(),
                    correctAnswer = "4",
                    explanation = "Number 1 is composed of one dot and four dashes: · - - - -. All numbers in Morse Code utilize 5 marks."
                )
            )
        ),
        Lesson(
            id = "mor_5",
            title = "Pro-signs & Spacing",
            description = "Understand the precise timing rules of word spacing and standard radio pro-signs.",
            moduleName = MODULE_MORSE,
            difficulty = "Advanced",
            questions = listOf(
                Question(
                    id = "q_mor_5_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    instruction = "Word Spacing Timing",
                    prompt = "To make Morse messages readable, how much space (measured in Dot-durations) is placed between entire words?",
                    options = listOf(
                        "7 dots",
                        "3 dots",
                        "1 dot",
                        "10 dots"
                    ),
                    correctAnswer = "0",
                    explanation = "The space between parts of the same letter is 1 dot. The space between letters is 3 dots. The space between words is 7 dots."
                ),
                Question(
                    id = "q_mor_5_2",
                    type = QuestionType.FILL_IN_BLANK,
                    instruction = "Invitation to Transmit",
                    prompt = "In radio transmission, the pro-sign code 'K' means 'Invitation to Transmit' or 'Go ahead'. In Morse, 'K' is written as - · ____.",
                    options = emptyList(),
                    correctAnswer = "-",
                    explanation = "The letter 'K' is dash-dot-dash (- · -), which is historically used to signal that the operator is ready to receive a response."
                )
            )
        )
    )

    fun getLessonsByModule(moduleName: String): List<Lesson> {
        return lessons.filter { it.moduleName == moduleName }
    }
}

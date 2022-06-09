## ViewModel

In this example, the logic of a simple quiz is to be implemented as a ViewModel.

The fragment with the layout for the quiz already exists ("GameFragment") and is to be connected to the ViewModel implementation.

### Task 1: NavigationGraph
As a repeat, the "MainActivity" is to be set up first as the host for our single page app and an associated NavigationGraph is to be created.

* Create NavigationGraph resource
* Use NavHostFragment in MainActivity
* A new ``StartFragment`` is to be used as navigation start. A button (TextView) with the label "Start Quiz" should be displayed there. Clicking on the button should navigate to the ``GameFragment``.
* ViewBinding" should be used to access the elements in the layout.

### Task 2: GameViewModel

The logic of the quiz game for the ``GameFragment`` is to be built and used step by step.

* The questions are not yet loaded via REST, but only generated hardcoded. To consider the future structure right away, a ``Question`` class should be used that corresponds to the JSON of a question as sent by the to-be REST API ([Open Trivia Database](https://opentdb.com)).
* Start with a simple ``GameViewModel`` and the representation of a single question in the ``GameFragment``.
* The answers should be displayed randomly distributed on the 4 answer buttons.
* When clicking on one of the answer buttons, the question should be "answered" internally and the button backgrounds should be adjusted according to the correctness of the answer.
* The question should only be able to be answered once.
* Then extend the ``GameViewModel`` to 10 questions.
* When a question is answered, clicking the ``Continue`` button should display the next question.
* When a question is answered, a timer shall be started and the remaining rate time shall be displayed via the ProgresssBar in the ``GameFragment``. After the rate time has expired, the question shall be judged as answered incorrectly if it was not answered by the user.
* After answering the last question a score shall be displayed on the ``Continue`` button instead of the label.
  And this in the format "Score: 3 / 10 correct".
* Update the backgrounds in the header:
  - The current unanswered question should be displayed with a blue background.
  - The current wrongly answered question should be displayed with blue background and red border.
  - The current correctly answered question should be displayed with blue background and green border
  - Wrongly answered questions (not current) should be displayed with red background
  - Correctly answered questions (not current) should be displayed with green background
  - Unanswered questions (not up to date) should be displayed with gray background

******************************************************************************* 

In diesem Beispiel soll die Logik eines einfachen Quiz als ViewModel implementiert werden. 

Das Fragment mit dem Layout für das Quiz ist bereits vorhanden ("GameFragment") und soll mit der ViewModel-Implementierung verbunden werden. 

### Task 1: NavigationGraph
Als Wiederholung soll zunächst die "MainActivity" als Host für unsere Single-Page-App eingerichtet werden und ein zugehöriger NavigationGraph erstellt werden. 

* NavigationGraph Ressource anlegen
* NavHostFragment in MainActivity verwenden
* Als Navigationsstart soll ein neues ``StartFragment`` verwendet werden. Dort soll ein Button (TextView) mit dem Label "Start Quiz" angezeigt werden. Bei Klick auf den Button soll auf das ``GameFragment`` navigiert werden. 
* Für den Zugriff auf die Elemente im Layout soll ``viewBinding`` verwendet werden. 

### Task 2: GameViewModel

Es soll Schritt für Schritt die Logik des Quiz-Spiels für das ``GameFragment`` aufgebaut und verwendet werden. 

* Die Fragen werden noch nicht via REST geladen, sondern nur hardcoded erzeugt. Um die zukünftige Struktur gleich zu berücksichtigen, soll eine ``Question`` Klasse verwendet werden, die dem JSON einer Frage entspricht, wie sie vom zu künftigen REST API geschickt wird ([Open Trivia Database](https://opentdb.com)).
* Beginnen Sie mit einem einfachen ``GameViewModel`` und der Darstellung einer einzelnen Frage im ``GameFragment``.
* Die Antworten sollten zufällig verteilt auf den 4 Antwort-Buttons angezeigt werden.
* Bei Klick auf einen der Antwort-Buttons soll die Frage intern "beantwortet" werden und die Button-Hintergründe entsprechend der Richtigkeit der Antwort angepasst werden.
* Die Frage sollte nur einmal beantwortet werden können.
* Erweitern Sie dann das ``GameViewModel`` auf 10 Fragen.
* Wenn eine Frage beantwortet ist, soll durch Klick auf den ``Continue``-Button die nächste Frage angezeigt werden.
* Bei der Beantwortung einer Frage soll ein Timer gestartet werden und die verbleibende Ratezeit über den ProgresssBar im ``GameFragment`` angezeigt werden. Nach Ablauf der Ratezeit soll die Frage als falsch beantwortet bewertet werden, falls sie nicht durch den Benutzer beantwortet wurde.
* Nach Beantwortung der letzten Frage soll auf dem ``Continue``-Button anstelle des Labels ein Score angezeigt werden.
  Und zwar im Format "Score: 3 / 10 correct".
* Update der Hintergründe im Header:
    - Die aktuelle unbeantwortete Frage soll mit blauem Hintergrund angezeigt werden
    - Die aktuelle falsch beantwortete Frage soll mit blauem Hintergrund und rotem Rand angezeigt werden
    - Die aktuelle richtig beantwortete Frage soll mit blauem Hintergrund und grünem Rand angezeigt werden
    - Falsch beantwortete Fragen (nicht aktuell) sollen mit rotem Hintergrund angezeigt werden 
    - Richtig beantwortete Fragen (nicht aktuell) sollen mit grünem Hintergrund angezeigt werden 
    - Unbeantwortete Fragen (nicht aktuell) sollen mit grauem Hintergrund angezeigt werden 


### Task 3: Anbindung REST Schnittstelle
In diesem Schritt soll die Liste der Fragen von einer REST Schnittstelle konsumiert werden. Wir verwenden dazu die frei verfügbare Quiz-Datenbank Open Trivia Database.

* Bestimmen der URL für die Abfrage von 10 Multiple-Choice Quizfragen aus beliebigen Kategorien.
* Im Manifest muss die Permission android.permission.INTERNET eingetragen werden.
* Einbinden der Retrofit Libraries für den Zugriff auf die REST Schnittstelle
* Definieren des API (Interface) für den Zugriff auf die TriviaDb Schnittstelle (über die gefundene URL).
* Konfigurieren und bauen der API Implementierung mittels Retrofit Builder.
* Verwenden des APIs im GameViewModel, um die Fragen bei Start des Spiels zu laden.
* Bei Verwendung von suspend functions darf das nicht im Main-Thread erfolgen!
* Aufräumen: Die vordefinierte Liste der Fragen (theQuestions) entfernen.
* Error-Handling: Bei Fehlern während der Kommunikation mit dem Rest API soll eine entsprechende Fehlermldung 
(weiß auf rotome Hintergrund) angezeigt werden
* Umstellen des API von "Call" auf Kotlin coroutines
* Einführen eines Query-Parameters für die Anzahl der Fragen (wir verwenden immer 10)
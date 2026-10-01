# Hod kostkou – XML vs. Compose

## Jak se aktualizuje zobrazená kostka

**XML + Kotlin (imperativní přístup):**
Rozhraní je definováno v `activity_main.xml`. V kódu si pomocí
`findViewById` získáme referenci na `TextView` (`tvDice`) a `Button`
(`btnRoll`). Kostku měníme přímo – nastavíme `tvDice.text = ...`
a tlačítko zakážeme voláním `btnRoll.isEnabled = false`. Časování zajišťuje
`Handler.postDelayed`. Za synchronizaci UI se starám sám.

**Jetpack Compose (deklarativní přístup):**
Rozhraní je funkce `DiceScreen()` v Kotlinu. Hodnota kostky a stav
tlačítka jsou *stavové proměnné* (`diceIndex`, `isRolling`). Do UI
nezasahuji přímo, jen změním stav a Compose sám překreslí (rekomponuje)
závislé prvky. Časování řeší korutina s `delay`.
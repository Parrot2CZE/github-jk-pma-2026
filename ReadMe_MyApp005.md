# MyApp005ObjednavkaV2 – Rezervace zbraně na Dny České zbrojovky (v2)

## Téma aplikace

Aplikace vychází z [MyApp004Rezervace](../MyApp004Rezervace), tedy z **rezervace pistole k vyzkoušení na Dnech České zbrojovky**. Oproti úkolu 004 je nově dvoudílná: na hlavní obrazovce se vybírá a **dynamicky počítá cena**, po potvrzení se výběr předá přes `Intent` na druhou obrazovku s rekapitulací.

| Prvek | Obsah |
|---|---|
| Radio buttony | CZ P-10 C / CZ P-09 C / CZ Shadow 2 Compact |
| Checkboxy | Kolimátor / Prodloužená hlaveň / Jsem držitel zbrojního oprávnění |
| Cena | Přepočítává se okamžitě při každé změně výběru |
| Tlačítko | **Rezervovat** (výběr času, uložení a přechod na rekapitulaci) |
| Fotografie | Mění se podle modelu a zaškrtnutých doplňků |

Ceny v aplikaci jsou ukázkové (konstanty v `MainActivity`): pistole 300 / 300 / 400 Kč, kolimátor +100 Kč, prodloužená hlaveň +150 Kč. Zbrojní oprávnění cenu neovlivňuje.

---

## Co se změnilo oproti úkolu 004

### Hlavní obrazovka (`MainActivity`)

1. **`ConstraintLayout`** místo `LinearLayout`.
2. **Dynamický přepočet ceny** pomocí lambda výrazů (`setOnCheckedChangeListener { _, _ -> refresh() }`) u `RadioGroup` a checkboxů. Cena se ukazuje nad tlačítkem.
3. **Předání dat přes `Intent`**: po potvrzení rezervace se na druhou obrazovku odešle model (`putExtra` String), doplňky a oprávnění (Boolean), cena (Int) a čas od–do.
4. Souhrn se **přesunul** z hlavní obrazovky na druhou, na hlavní zůstala jen cena.

### Obrazovka rekapitulace (`SummaryActivity`)

1. **`ScrollView`** (`android:fillViewport="true"`) obalující vnitřní `ConstraintLayout`.
2. **Příjem dat z Intentu** (`getStringExtra`, `getBooleanExtra`, `getIntExtra`) a sestavení textového přehledu: vybraná pistole, čas, doplňky, oprávnění ano/ne a celková cena.
3. **Podmínky rezervace** jako dlouhý text, který demonstruje scrollování.
4. Tlačítko **Zpět k úpravě** (`finish()`) ukotvené na spodní hraně obrazovky.

---

## Návazný úkol – trvalé ukládání objednávek

Rezervace se nyní trvale ukládají pomocí `SharedPreferences` (záznam `model;začátek;konec`). Zvažované možnosti pro další rozšíření:

| Možnost | Vhodné pro |
|---|---|
| SharedPreferences / DataStore | malé množství jednoduchých dat (současné řešení) |
| Soubor (např. JSON) | strukturovaná data bez databáze |
| Room (SQLite) | seznam objednávek, dotazy, řazení a historie |
| Vzdálená databáze / API | sdílení mezi zařízeními |

Pro seznam objednávek s dotazy a historií se jako nejvhodnější jeví **Room**.

## Screenshoty

Jsou uložené v Screenshots/MyApp005/
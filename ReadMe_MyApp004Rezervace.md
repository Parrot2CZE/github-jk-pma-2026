# MyApp004Rezervace – Rezervace zbraně na Dny České zbrojovky

## Téma aplikace

Místo kol z ukázkového zadání jsem zvolil **rezervaci pistole k vyzkoušení na Dnech České zbrojovky** na místní střelnici. Uživatel si vybere model, upřesní konfiguraci, vybere časový úsek a aplikace rezervaci uloží a vypíše do souhrnu.

| Prvek | Obsah |
|---|---|
| Radio buttony | CZ P-10 C / CZ P-09 C / CZ Shadow 2 Compact |
| Checkboxy | Kolimátor / Prodloužená hlaveň / Jsem držitel zbrojního oprávnění |
| Tlačítko | **Rezervovat** (otevře výběr času) |
| Souhrn | Výpis zbraně, doplňků, oprávnění (ano/ne) a času po potvrzení rezervace |
| Fotografie | Mění se podle modelu a zaškrtnutých doplňků |

---

### Vlastní funkční rozšíření

1. **Ukládání rezervací a kontrola kolizí.** Rezervace se trvale ukládají (`SharedPreferences`) a **stejnou zbraň nelze rezervovat dvakrát na překrývající se čas**. Při konfliktu se zobrazí hláška a dialog zůstane otevřený, aby šlo vybrat jiný čas. Návazné rezervace (např. 10:00–11:00 a 11:00–12:00) jsou povolené.
2. **Výběr časového rozsahu** (začátek a konec) po 30 minutách v intervalu 10:00–20:00. Začátek lze zvolit 10:00 až 19:30, konec vždy až po začátku (nejdříve 10:30, nejpozději 20:00), takže nejde vybrat neplatný rozsah.
3. **Fotografie reaguje na konfiguraci**: kolimátor a prodloužená hlaveň mění zobrazený obrázek (tam, kde mám pro daný model odpovídající fotografii).
4. **Kontrola vstupu**: bez vybrané zbraně rezervace neproběhne a zobrazí se hláška.
5. **Mazání rezervací** (volba v menu „⋮“ v horní liště) pro snadné testování.

## Screenshoty

Jsou uložené v Screenshots/MyApp004/
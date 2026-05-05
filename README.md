# Oslo Clojure Meetup

Dette er repoet for den statiske nettsida du finner på https://osloclojuremeetup.github.io/.

Her finner du en oversikt over tidligere meetups med tema, talere, dato, og referanser 😊

Meetups arrangeres gjennom [Clojure/Oslo på Meetup.com](https://www.meetup.com/clojure-oslo/) og annonseres i kanalen #clojure-norway på [Clojurians-slacken](http://clojurians.net/).

## Bidra

Vi synes det er kjempehyggelig om du vil bidra til nettsida!

Per i dag, er det et Clojure-prosjekt som generer en statisk nettside som vi så pusher ut på GitHub pages.

### Starte server med live reload

For utvikle lokalt, kan du clone dette repoet og koble opp din favoritteditor slik du pleier.

Når man skal utvikle lokalt, er det hyggelig med rask feedback. Da kan du starte en liten server som leverer HTML direkte over HTTP med live reload så du umiddelbart kan se endringene dine.

Start serveren og åpne browseren med

```clojure
(lifecycle/browse)
```

Start/stop den igjen hvis du trenger med
```clojure
(lifecycle/start)
(lifecycle/stop)
```

Og hvis du vil bygge siden "ordentlig" og se på den ferdigmasserte HTMLen, så kan du kjøre
```clojure
(site/build)
```

Fordi vi synes det er kjekt å kunne starte ting kjapt, har vi lagt ved noen [Babashka](https://babashka.org/) tasks som du kan kjøre for å slippe å hoppe mellom filene.
```clojure
- browse: Start serveren og åpne nettleseren på riktig addresse
- start: Start serveren
- stop: Stop serveren
- nvk-reload: Reload all koden
- nvk-test: Kjør alle testene
```

De kan du kjøre med `bb <navn på task>` fra terminalen.

Men det er jo spesielt hyggelig å sette opp støtte for det i editoren din. Her kan du finne en Emacs-snutt fra teamet på Mattilsynet, for eksempel: https://github.com/magnars/emacsd-reboot/blob/main/packages/setup-babashka-task-mode.el

### Legge til nytt meetup

Meetups er bare [EDN](https://github.com/edn-format/edn) filer, altså ren data, så å legge til et nytt meetup er så enkelt som å lage en ny fil i `./meetups`-mappa.

Meetup-filene sine navn er datoen meetupen var på, på formatet 'YYYY-MM-DD' med `.edn` extension. For eksempel, '2026-10-25' for 25. oktober 2026.

Ellers har hvert meetup samme form:
```clojure
{:meetup/date "YYYY-MM-DD"
 :meetup/title "Din meetup tittel"
 :meetup/description "En kort beskrivelse"
 :meetup/agenda [{:talk/title "En talk"
                  :talk/speakers #{ {:github/id "teodorlu"}
                                    {:github/id "SophieBosio"} }}]}
```

`:meetup/date` er datoen meetupen skjedde.
`:meetup/title` er en tittel på meetupen.
`:meetup/description` er en kort beskrivelse.
`:meetup/agenda` er en liste over talks/innlegg på meetupen. Disse har sin egen tittel og noen speakers/talere.

Talerne referes til med `:github/id` og hvis ikke taleren allerede er registrert, må de legges til i `./speakers.edn`.

### Litt om teknologien

Vi har gått hardt inn og lagd en ordentlig Clojure-app. Den har en in-memory [Datomic](https://www.datomic.com/) database som vi leser inn meetups til.

(Hvis du har lagt til en meetup som ikke synes, så prøv å reevaluere `site`-navnerommet, der databasen defineres og vi fyller den med meetups.)

Det er kanskje litt overkill for det vi har i dag, men det gir oss validering av alle verdiene i alle filene i `./meetups`-mappa - OG! Det gjør dataen veldig hyggelig å jobbe med!

Vi kan også bruke Datomic til å utvikle nye features - hvis du har en god idé, kjør på!


Vi bruker også [Datastar](https://data-star.dev/) til å pushe ny HTML og CSS når vi utvilker lokalt, som gir oss en kjempetett feedback loop!

Nederst i `frontpage.clj`, finner du denne kodesnutten:
```clojure
(when-let [db @state/!db]
  (sse/push-hiccup! [:div#morph (render-body db)]))
```

Hvis du evaluerer hele fila, får du pusha ny HTML til nettleseren umiddelbart!

CSS'en oppdateres automatisk når du endrer filene via `lifecycle.assetwatch`.

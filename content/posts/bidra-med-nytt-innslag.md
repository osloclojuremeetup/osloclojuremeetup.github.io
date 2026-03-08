---
date: '2026-03-08T21:16:13+01:00'
draft: false
title: 'Bidra Med Nytt Innslag'
---

Hei og velkommen til Oslo Clojure Meetup sin nettside! Her har vi en oversikt over tidligere meetups og litt info om dem.

Under følger en kort guide for å bidra med et nytt innslag.

## To framgangsmåter

Denne nettsiden er bygd med [Hugo](https://gohugo.io/), et verktøy for å genere statiske nettsider.

I deres [quick start guide](https://gohugo.io/getting-started/quick-start) finnes det instruksjoner for å installere og komme i gang med Hugo.

Du kan enten bruke `hugo` direkte, eller så kan du bruke det lille scriptet `./ocm.sh` som du finner i dette repoet.

### Quick Start med `./ocm.sh`

Du må gjerne lese hele posten, men hvis du helst vil komme i gang så fort som mulig, er dette det du trenger å gjøre:

1. `chmod +x ./ocm.sh` for å gjøre `./ocm.sh` eksekverbar
2. `./ocm.sh new-post <Tittlen På Ditt Innslag>` for å opprette en ny post
3. Fyll inn hver av seksjonene i den nye fila `./content/posts/<tittelen-på-ditt-innslag>.md`
4. `./ocm.sh preview` for å forhåndsvise posten
5. Set `draft: false` i frontmatter'et på din post, som ligger i `./content/posts/<tittelen-på-ditt-innslag>.md`
6. `./ocm.sh build`
7. Commit og push! Når endringen er i `master`, deployes siden automatisk 🥳

## Opprette en ny post

Hver post en enkel Markdown fil som ligger i `./content/posts/<tittel-på-ditt-innslag>.md`.

Du kan generere en ny fil ved å bruke Hugo,

`hugo new content content/posts/<tittel-på-ditt-innslag>.md`

eller med `./ocm.sh`,

`./ocm.sh new-post <Tittel På Ditt Innslag>`

Da får du lagd en Markdown fil som starter med litt metadata. `./ocm.sh` lager også overskrifter for deg.

```markdown
---
title: 'Tittel På Ditt Innslag'
date: '2026-01-01'
draft: true
---
```

## Format

For hver meetup, vil vi gjerne ha med:
- Tema
- Hvem som snakket
- Dato
- Referanser (for eksempel lenker til relevante artikler, talks, repoer, kodesnutter osv.)
- Opptak, hvis det finnes

Her er et forslag du kan bruke til inspirasjon:

```markdown
---
title: 'Oslo Socially Functional Reboot: Functional Core / Imperative Shell'
date: '2026-02-24'
draft: true
---

### Tema

Funksjonell kjerne/imperativt skall; hvordan å anvende funksjonell programmering gjennom en hel programmvarearkitektur.

Konfliktfrie repliserte datatyper og API-design; unngå problemer med mutasjon og samtidig skriving.

### Talere

Magnar Sveen: "Funksjonell kjerne, imperativt skall: en arkitektur på vranga"
Teodor Elstad: "Can Conflict-free Replicated Data-Types teach us anything about API-design?"

### Dato

24. februar 2026

### Referanser

Opptak av "Funksjonell kjerne, imperativt skall" på JavaZone:
[En arkitektur på vranga på JavaZone](https://parenteser.mattilsynet.io/fkis-jz/)

Paper nevnt under "Can Conflict-free Replicated Data-Types teach us anything about API-design?"
[A comprehensive study of Convergent and Commutative
Replicated Data Types](https://inria.hal.science/inria-00555588v1/document)

Bloggpost skrevet av Teodor Heggelund etter meetup'en:
[Intensjon, instruks og effektuering: byggesteiner i effektsystemer](https://parenteser.mattilsynet.io/intensjon-instruks-effektuering/)
```

## Forhåndsvisning av posten din

Hvis du vil, så kan du nå forhåndsvise posten din!

Det gjør du med enten,

`hugo hugo server --buildDrafts --navigateToChanged`

eller

`./ocm.sh preview`

som begge starter opp en liten server på `localhost:1313` for deg!

## Publisere posten din

Når du er fornøyd med posten din, så er det bare ett steg igjen: Publisere!

Hvis du er skarp, la du kanskje merke til den lille `draft: true`-greia i frontmatter'et? Den er der slik at man kan jobbe med utkast uten at man ved uhell publiserer posten når man pusher branchen sin.

Men nå som vi er klare, kan vi sette `draft` til `false` og så fyre løs med enten

`hugo`

eller

`./ocm.sh build`!


Da genereres det en tilsvarende HTML fil i `./public/posts/<tittlen-på-ditt-innslag>/index.html`.

Da er det bare å committe og pushe til GitHub, og når endringen din er i `master`, så deployes siden automatisk 🥳

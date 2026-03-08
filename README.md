# Oslo Clojure Meetup

En enkel statisk nettside for Oslo Clojure Meetup.

Her finner du oversikt over tidligere meetups med tema, talere, dato, og referanser 😊

Meetups arrangeres gjennom [Clojure/Oslo på Meetup.com](https://www.meetup.com/clojure-oslo/) og annonseres i kanalen #clojure-norway på [Clojurians-slacken](http://clojurians.net).


## Bidra med nytt innslag

I posten https://osloclojuremeetup.github.io/posts/bidra-med-nytt-innslag/, finner du mer detaljerte instruksjoner, men her er en quick start guide hvor vi bruker `./ocm.sh`, et lite shell script for å lage nye posts for Oslo Clojure Meetup:

1. `chmod +x ./ocm.sh` for å gjøre `./ocm.sh` eksekverbar
2. `./ocm.sh new-post <Tittlen På Ditt Innslag>` for å opprette en ny post
3. Fyll inn hver av seksjonene i den nye fila `./content/posts/<tittelen-på-ditt-innslag>.md`
4. `./ocm.sh preview` for å forhåndsvise posten
5. Set `draft: false` i frontmatter'et på din post, som ligger i `./content/posts/<tittelen-på-ditt-innslag>.md`
6. `./ocm.sh build`
7. Commit og push! Når endringen er i `master`, deployes siden automatisk 🥳

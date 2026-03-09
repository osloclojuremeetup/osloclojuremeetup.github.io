#!/bin/bash

set -e

command="${1:-}"

new_post() {
    if [ -z "$2" ]; then
        echo "Bruk: $0 new-post <Tittlen På Ditt Innslag>"
        exit 1
    fi

    shift
    title="$*"

    slug=$(echo "$title" | tr '[:upper:]' '[:lower:]' | sed 's/ /-/g' | sed 's/[^a-z0-9æøå-]//g')

    filepath="./content/posts/${slug}.md"

    if [ -f "$filepath" ]; then
        echo "Feil: Det finnes allerede en fil med samme navn i $filepath"
        exit 1
    fi

    date=$(date +%Y-%m-%d)

    cat > "$filepath" <<EOF
---
title: '$title'
date: '$date'
draft: true
---

### Tema

### Talere

### Dato

### Referanser
EOF

    echo "Opprettet en ny post i $filepath"
}

preview() {
    hugo server --buildDrafts --navigateToChanged
}

build() {
    hugo
}

case "$command" in
    new-post)
        new_post "$@"
        ;;
    preview)
        preview
        ;;
    build)
        build
        ;;
    *)
        echo "Bruk: $0 {new-post|preview|build}"
        echo ""
        echo "Commands:"
        echo "  new-post <Title>  Opprett en ny post"
        echo "  preview           Start Hugo-serveren og vis alt, inkludert drafts"
        echo "  build             Bygg siden og alle posts"
        exit 1
        ;;
esac

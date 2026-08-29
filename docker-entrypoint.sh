#!/bin/sh
set -eu

active_profiles=",${SPRING_PROFILES_ACTIVE:-},"
case "$active_profiles" in
*,public,*|*,render,*)
    database_dir="${PUBLIC_DB_DIR:-/tmp/scorecard-db}"
    case "$database_dir" in
        /tmp/*|/var/tmp/*|/app/runtime/*)
            ;;
        *)
            echo "PUBLIC_DB_DIR must be under /tmp, /var/tmp or /app/runtime" >&2
            exit 1
            ;;
    esac

    mkdir -p "$database_dir"
    if [ ! -f /app/public-seed/devdb.properties ] || [ ! -f /app/public-seed/devdb.script ]; then
        echo "Public database seed is incomplete" >&2
        exit 1
    fi
    for suffix in properties script data backup log lck tmp; do
        rm -f "$database_dir/devdb.$suffix"
    done
    for seed_file in /app/public-seed/devdb.*; do
        [ -f "$seed_file" ] || continue
        cp "$seed_file" "$database_dir/"
    done
    echo "Restored versioned database seed for profile ${SPRING_PROFILES_ACTIVE}"
    ;;
esac

exec java -Dserver.port="${PORT:-8080}" -jar /app/app.war

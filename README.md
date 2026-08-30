# Golf Skin Games

Spring Boot app for managing golf skin games with rounds, scorecards, and leaderboard.

## Features
- Games and rounds (date, course, bet)
- Courses and par values per hole
- Players with optional HCP and email
- Scorecards with OUT/IN/TOT sums and live updates
- Leaderboard across rounds
- Responsive score entry (desktop + mobile grids)
- Import tools:
  - Scores import by pasted numbers (supports OUT/IN/TOT checks)
  - Players import by list of "FirstName LastName" per line
- Admin login with read-only mode for guests

## Use Cases
- Create a game, add rounds, and enter scores
- Maintain course par values
- Manage players and their details
- View score list per round
- View leaderboard across rounds
- Import existing scorecards from copied text
- Import players from a text list

## Admin Login / Read-only Mode
- Without login the UI is read-only.
- Mutating actions (add, edit, delete, save, import) are disabled or blocked.
- Admin login uses `ADMIN_USERNAME` and `ADMIN_PASSWORD` environment variables.
- Local development falls back to the values in `application.properties`.
- Login: `/admin/login`
- Logout: `/admin/logout`

## Score Import Format
Paste numbers separated by spaces, tabs, or new lines.

Supported formats:
- 18 numbers (holes 1-18)
- 21 numbers (holes 1-9, OUT, holes 10-18, IN, TOT)

Example:
```
4 4 3 4 3 7 5 3 4 37
5 5 4 2 4 4 5 4 3 36
73
```

## Player Import Format
One player per line:
```
Matti Meikalainen
Teemu Teurastaja
Annika Sorsson
```
- HCP is set to 0
- Email is left empty
- Duplicate (first + last) names are skipped

## Running Locally

### Default (HSQLDB file)
Uses `src/main/resources/application.properties`:
```
mvn spring-boot:run
```

### HSQL Database Manager

Stop the application before opening the file-based database. On Windows,
launch HSQL Database Manager Swing from the project root with:

```powershell
Start-Process -FilePath "javaw.exe" -ArgumentList @('-cp', "$env:USERPROFILE\.m2\repository\org\hsqldb\hsqldb\2.7.3\hsqldb-2.7.3.jar", 'org.hsqldb.util.DatabaseManagerSwing', '--driver', 'org.hsqldb.jdbc.JDBCDriver', '--url', "jdbc:hsqldb:file:$($PWD.Path.Replace('\', '/'))/data/hsqldb/devdb", '--user', 'SA')
```

The local database user is `SA` and its password is empty.

### Postgres profile
```
SPRING_PROFILES_ACTIVE=postgres mvn spring-boot:run
```
Configuration is in:
- `src/main/resources/application-postgres.properties`

### Render profile with ephemeral admin access
```
SPRING_PROFILES_ACTIVE=render ADMIN_USERNAME=admin ADMIN_PASSWORD=secret mvn spring-boot:run
```

The Docker entrypoint restores the versioned database before each start. Admin
login is available, but changes made in the browser disappear on the next
restart or deploy. Configure the exact environment variable names
`ADMIN_USERNAME` and `ADMIN_PASSWORD` in Render and redeploy after changing
them. The `render` profile intentionally fails to start if either value is
missing, so the local default password cannot become public accidentally.

### Public read-only profile

The public profile disables admin login and every mutating route. It expects a
file-based HSQL database at `/tmp/scorecard-db/devdb` by default:

```
SPRING_PROFILES_ACTIVE=public mvn spring-boot:run
```

## Docker Compose (Postgres)
Start database:
```
docker compose up -d
```
Then run the app with the Postgres profile:
```
SPRING_PROFILES_ACTIVE=postgres mvn spring-boot:run
```

## Public Render deployment

`render.yaml` creates a free Docker Web Service in Frankfurt, activates the
`public` profile and checks `/actuator/health`. The container restores the
versioned HSQL snapshot before every start, so runtime changes never become
part of the next deployment.

An existing manually configured Render service can continue to use the
`render` profile. Both `render` and `public` restore the same versioned seed;
only `render` exposes admin login.

Before updating the public snapshot, stop the local application cleanly. Then
run:

```powershell
./scripts/update-public-seed.ps1
```

The export is built from a temporary copy: it does not modify the local
database. Email addresses, external user IDs and external competitor IDs are
removed from the public copy. Review the snapshot change, run `mvn test`, and
push the commit. Render then builds and publishes the new image automatically.

For a local container smoke test:

```
docker build -t golfapp .
docker run --rm -e SPRING_PROFILES_ACTIVE=public -p 8080:8080 golfapp
```

Open `http://localhost:8080/actuator/health` and a leaderboard page before
publishing.

## Notes
- The app uses a session flag to determine admin status.
- If a mutating endpoint is requested without admin login, it redirects to `/admin/login`.
- In the public profile, admin login is absent and mutating endpoints return HTTP 403.

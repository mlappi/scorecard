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
- Admin login uses credentials from `application.properties`:
  - `admin.username`
  - `admin.password`
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

### Postgres profile
```
SPRING_PROFILES_ACTIVE=postgres mvn spring-boot:run
```
Configuration is in:
- `src/main/resources/application-postgres.properties`

### Render profile
```
SPRING_PROFILES_ACTIVE=render mvn spring-boot:run
```
Configuration is in:
- `src/main/resources/application-render.properties`

## Docker Compose (Postgres)
Start database:
```
docker compose up -d
```
Then run the app with the Postgres profile:
```
SPRING_PROFILES_ACTIVE=postgres mvn spring-boot:run
```

## Dockerfile (Render)
A Dockerfile is provided for Render Web Services.
It builds the app with Maven and runs the WAR:
```
docker build -t golfapp .
docker run -e PORT=8080 -p 8080:8080 golfapp
```

## Notes
- The app uses a session flag to determine admin status.
- If a mutating endpoint is requested without admin login, it redirects to `/admin/login`.
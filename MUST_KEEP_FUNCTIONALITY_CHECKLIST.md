# Must-Keep Functionality Checklist

This checklist is derived from current controllers/services and should remain true after project simplification.

## 1) Authentication and Session

- `POST /login` authenticates by username/password and stores `userInfo` in HTTP session.
- `POST /logout` invalidates session and redirects to `/`.
- Failed login redirects to `/?error=true`.
- All non-public routes require authentication.
- Public routes stay public: `/`, `/login`, `/logout`, `/register`, `/forgot`, `/forgot/verify`, `/forgot/reset`, static assets.

## 2) Registration

- `GET /register` renders register form with secret questions.
- `POST /register` validates form, creates user, and redirects to `/` with success flash message.
- Duplicate username/email returns to register page with an error.

## 3) Forgot Password Flow

- `GET /forgot` renders email form.
- `POST /forgot` finds account by email and shows secret question.
- `POST /forgot/verify` validates secret answer and transitions to reset form.
- `POST /forgot/reset` validates reset form and updates encoded password.
- Expired flow/user-not-found scenarios return to forgot form with a clear error.

## 4) User Self-Service

- `GET /dashboard` renders dashboard for authenticated user.
- `GET /user-options` renders user options page.
- `GET /edit-profile` preloads current user profile + secret questions.
- `POST /edit-profile` updates profile and secret question/answer, with validation errors shown.
- `GET /change-password` renders change password form.
- `POST /change-password` validates old/new password and logs user out after success.

## 5) Search

- API search: `POST /api/search` with `SearchRequestDTO` returns `SearchResultDTO`.
- Book endpoint search: `POST /books/search` returns search results JSON.
- UI search pages support:
  - `GET /search/author`
  - `GET /search/publisher`
  - `GET /search/title`
  - redirect helpers: `/search/search-author`, `/search/search-publisher`, `/search/search-title`
- Query validation preserves minimum length behavior (>= 2 chars).
- Pagination/sorting parameters remain supported (`page`, `size`, `sort`, `direction`, `matchType`).

## 6) Catalog Browsing

- `GET /authors` and `GET /authors/{id}` work with author summary data + paged books.
- `GET /categories` and `GET /categories/{id}` work with category summary + paged books.
- `GET /publishers` and `GET /publishers/{id}` work with publisher summary + paged books.
- `GET /books` supports filtering by `authorId`, `publisherId`, `categoryId`.
- `GET /book-details/{id}` shows book details, features, related books by author/publisher.
- `GET /compare?bookIds=...` compares up to 5 books and shows feature matrix.

## 7) Admin - Users

- `GET /admin/panel` renders admin landing page.
- `GET /admin/users` paginates users.
- `GET /admin/users/add` and `POST /admin/users/add` add users.
- `GET /admin/users/edit/{id}` and `POST /admin/users/edit/{id}` edit users.
- `POST /admin/users/delete` deletes selected users.
- Admin user forms still include secret questions and user type handling.

## 8) Admin - Authors/Publishers/Distributors/User Types

- Authors CRUD:
  - `GET /admin/authors`
  - `GET|POST /admin/authors/add`
  - `GET|POST /admin/authors/edit/{id}`
  - `POST /admin/authors/delete`
- Publishers CRUD (entity type 1):
  - `GET /admin/publishers`
  - `GET|POST /admin/publishers/add`
  - `GET|POST /admin/publishers/edit/{id}`
  - `POST /admin/publishers/delete`
- Distributors CRUD (entity type 2):
  - `GET /admin/distributors`
  - `GET|POST /admin/distributors/add`
  - `GET|POST /admin/distributors/edit/{id}`
  - `POST /admin/distributors/delete`
- User type CRUD:
  - `GET /admin/user-types`
  - `GET|POST /admin/user-types/add`
  - `GET|POST /admin/user-types/edit/{id}`
  - `POST /admin/user-types/delete`

## 9) Core Data Rules to Preserve

- Passwords are stored encoded (BCrypt).
- Registration/profile/admin edit preserve email uniqueness checks.
- Username uniqueness is preserved on registration.
- Search response shape remains compatible with existing pages/templates.
- Book summary generation still includes author/publisher/category display data.

## 10) Minimum Regression Test Set

- Auth: login success/failure, logout.
- Register: success, duplicate username, duplicate email, validation error.
- Forgot flow: email not found, answer wrong, reset success.
- Change password: wrong old password, success forces re-login.
- Search API: author/publisher/title queries with pagination.
- Catalog pages: author/category/publisher detail pages render with paged books.
- Book details and compare page render expected sections.
- Admin users CRUD happy path + validation error path.
- Admin authors/publishers/distributors/user-types CRUD happy path.

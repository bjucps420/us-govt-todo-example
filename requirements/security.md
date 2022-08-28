| ID | SEC-1 |
| --- | --- |
| Function | Limit General Public Users Listing |
| Description | A general public user visits the application. |
| Inputs | None |
| Source | Request of General Public User to View Todos |
| Outputs | A list of Todos filtered to show only unclassified Todos |
| Action | A general public user visits the application.  The application issues a query to the backend for only unclassified todos.  The results of the query are displayed on the screen.  An add button is shown allowing the adding of new todos.  No edit or deletion functionality is shown. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-2 |
| --- | --- |
| Function | Limit General Public Users Viewing |
| Description | A general public user views a todo. |
| Inputs | Id of Todo |
| Source | Request of General Public User to View a Todo |
| Outputs | A Todo or Access Denied |
| Action | A general public user visits the application.  They request to view a Todo. The Todo is displayed only if the level is unclassified. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-3 |
| --- | --- |
| Function | Limit General Public Users Actions |
| Description | General Public Users cannot do other actions. |
| Inputs | None |
| Source | None |
| Outputs | None |
| Action | No other actions except those previously stated are permissible for General Public Users. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-4 |
| --- | --- |
| Function | Limit Classified Users Listing |
| Description | A Classified user visits the application. |
| Inputs | None |
| Source | Request of Classified User to View Todos |
| Outputs | A list of Todos filtered to show only classified or unclassified Todos |
| Action | A classified user visits the application.  The application issues a query to the backend for both classified and unclassified todos.  The results of the query are displayed on the screen.  An add button is shown allowing the adding of new todos.  No edit or deletion functionality is shown. |
| Requires | A logged in Classified User |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-5 |
| --- | --- |
| Function | Limit Classified Users Viewing |
| Description | A Classified user views a todo. |
| Inputs | Id of Todo |
| Source | Request of Classified User to View a Todo |
| Outputs | A Todo or Access Denied |
| Action | A classified user visits the application.  They request to view a Todo. The Todo is displayed only if the level is classified or unclassified. |
| Requires | A logged in Classified User |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-6 |
| --- | --- |
| Function | Limit Classified Users Actions |
| Description | Classified Users cannot do other actions. |
| Inputs | None |
| Source | None |
| Outputs | None |
| Action | No other actions except those previously stated are permissible for Classified Users. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-7 |
| --- | --- |
| Function | Limit Secret Users Listing |
| Description | A Secret User visits the application. |
| Inputs | None |
| Source | Request of Secret User to View Todos |
| Outputs | A list of Todos filtered to show only secret, classified or unclassified Todos |
| Action | A secret user visits the application.  The application issues a query to the backend for secret, classified, and unclassified todos.  The results of the query are displayed on the screen.  An add button is shown allowing the adding of new todos.  No edit or deletion functionality is shown. |
| Requires | A logged in Secret User |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-8 |
| --- | --- |
| Function | Limit Secret Users Viewing |
| Description | A Secret User views a todo. |
| Inputs | Id of Todo |
| Source | Request of Secret User to View a Todo |
| Outputs | A Todo or Access Denied |
| Action | A secret user visits the application.  They request to view a Todo. The Todo is displayed only if the level is secret, classified, or unclassified. |
| Requires | A logged in Secret User |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-9 |
| --- | --- |
| Function | Limit Secret Users Actions |
| Description | Secret Users cannot do other actions. |
| Inputs | None |
| Source | None |
| Outputs | None |
| Action | No other actions except those previously stated are permissible for Secret Users. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-10 |
| --- | --- |
| Function | Limit Top Secret Users Listing |
| Description | A Top Secret User visits the application. |
| Inputs | None |
| Source | Request of Top Secret User to View Todos |
| Outputs | A list of Todos filtered to show only top secret, secret, classified or unclassified Todos |
| Action | A top secret user visits the application.  The application issues a query to the backend for top secret, secret, classified, and unclassified todos.  The results of the query are displayed on the screen.  An add button is shown allowing the adding of new todos.  No edit or deletion functionality is shown. |
| Requires | A logged in Top Secret User |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-11 |
| --- | --- |
| Function | Limit Top Top Secret Users Viewing |
| Description | A Top Secret User views a todo. |
| Inputs | Id of Todo |
| Source | Request of Top Secret User to View a Todo |
| Outputs | A Todo or Access Denied |
| Action | A top secret user visits the application.  They request to view a Todo. The Todo is displayed only if the level is top secret, secret, classified, or unclassified. |
| Requires | A logged in Top Secret User |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-12 |
| --- | --- |
| Function | Limit Top Secret Users Actions |
| Description | Top Secret Users cannot do other actions. |
| Inputs | None |
| Source | None |
| Outputs | None |
| Action | No other actions except those previously stated are permissible for Top Secret Users. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-13 |
| --- | --- |
| Function | Limit Aid Users List |
| Description | An Aid User visits the application. |
| Inputs | None |
| Source | Request of Aid User |
| Outputs | A list of Todos filtered to show only top secret, secret, classified or unclassified Todos |
| Action | An Aid user visits the application.  The application issues a query to the backend for top secret, secret, classified, and unclassified todos.  The results of the query are displayed on the screen. Edit or deletion functionality is shown. No adding ability is shown. |
| Requires | A logged in Aid User |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-14 |
| --- | --- |
| Function | Limit Aid Users Viewing |
| Description | An Aid User views a todo. |
| Inputs | Id of Todo |
| Source | Request of Aid User to View a Todo |
| Outputs | A Todo or Access Denied |
| Action | An aid user visits the application.  They request to view a Todo. The Todo is displayed only if the level is top secret, secret, classified, or unclassified. |
| Requires | A logged in Aid User |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-15 |
| --- | --- |
| Function | Limit Aid Users Editing |
| Description | An Aid User edits a todo. |
| Inputs | Id of Todo |
| Source | Request of Aid User to Edit a Todo |
| Outputs | A Todo or Access Denied |
| Action | An aid user visits the application.  They request to view a Todo then request to edit. The Todo is displayed only if the level is top secret, secret, classified, or unclassified.  The edit succeeds if the user can view the todo. |
| Requires | A logged in Aid User |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-16 |
| --- | --- |
| Function | Limit Aid Users Deleting |
| Description | An Aid User edits a todo. |
| Inputs | Id of Todo |
| Source | Request of Aid User to Edit a Todo |
| Outputs | A Todo or Access Denied |
| Action | An aid user visits the application.  They request to view a Todo then request to delete. The Todo is displayed only if the level is top secret, secret, classified, or unclassified.  The delete succeeds if the user can view the todo. |
| Requires | A logged in Aid User |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-17 |
| --- | --- |
| Function | Limit Aid Users Actions |
| Description | Aid Users cannot do other actions. |
| Inputs | None |
| Source | None |
| Outputs | None |
| Action | No other actions except those previously stated are permissible for Aid Users. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-18 |
| --- | --- |
| Function | Require Login for Classified, Secret, Top Secret and Aid Users |
| Description | Any user that can access classified information or higher requires login. |
| Inputs | None |
| Source | None |
| Outputs | None |
| Action | Users that have permission to access classified, secret, or top secret todos must login before accessing those items. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-19 |
| --- | --- |
| Function | Password Requirements |
| Description | Passwords may not be easily guessable. |
| Inputs | None |
| Source | None |
| Outputs | None |
| Action | Passwords must be at least 16 characters in length, have 1 letter, 1 number and 1 symbol. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-20 |
| --- | --- |
| Function | Two Factor Auth Requirements |
| Description | All users must have the ability to enable 2-FA enabled. |
| Inputs | None |
| Source | None |
| Outputs | None |
| Action | 6-digit OTP two factor authentication must be enable-able for all accounts. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | SEC-21 |
| --- | --- |
| Function | Storage Requirements |
| Description | Passwords and OTP Must Be Stored Securely |
| Inputs | None |
| Source | None |
| Outputs | None |
| Action | Fusion Auth must be the only place that passwords and OTP information is stored. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

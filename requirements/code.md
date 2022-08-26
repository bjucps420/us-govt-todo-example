| ID | CODE-1 |
| --- | --- |
| Function | List Todos |
| Description | A request is made to list todos. |
| Inputs | Clearance Level of User |
| Source | User |
| Outputs | A list of Todos filtered by the Clearance Level |
| Action | A user visits the application.  The application issues a query to the backend for todos filtered by the user's clearance level. The results are then shown to the user.  The ability add new todos, edit existing todos, and delete todos may be shown based on the user's clearance. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | CODE-2 |
| --- | --- |
| Function | View Todo |
| Description | A request is made to view a todo. |
| Inputs | Clearance Level of User, ID of Todo |
| Source | User |
| Outputs | A Todo is shown or the User is denied access |
| Action | A user visits the application. The user requests to see a Todo's details. A request is issued to the database to fetch the todo filtering todo's the user does not have access to.  If the todo is found, it is displayed.  The option to edit or delete the todo may be shown based on the user role.  If the todo is not found, the user is shown an access denied message. |
| Requires | None |
| Precondition | NOne |
| Postcondition | None |
| Side Effects | None |

| ID | CODE-3 |
| --- | --- |
| Function | Edit Todo |
| Description | A request is made to edit a todo. |
| Inputs | Clearance Level of User, ID of Todo |
| Source | User |
| Outputs | The Todo is edited, and the user goes to the view screen. |
| Action | A user visits the application. The user requests to see a Todo's details. The user clicks the edit button. The details of the todo become editable.  The user is then allowed to change the details of the todo (including the todo's status).  The user clicks save.  The user is redirected to the viewing screen so they may confirm their changes. |
| Requires | A Logged-in User with the Role of Aid |
| Precondition | The Todo exists in the database |
| Postcondition | The Todo still exists in the database |
| Side Effects | The Todo in the database is modified to match the edits |

| ID | CODE-4 |
| --- | --- |
| Function | Delete Todo |
| Description | A request is made to delete a todo. |
| Inputs | Clearance Level of User, ID of Todo |
| Source | User |
| Outputs | The Todo is deleted, and the user goes to the listing screen |
| Action | A user visits the application. The user requests to see a Todo's details. The user clicks the delete button. The application produces a modal asking for confirmation of the action and warning that the action cannot be undone.  The user confirms the action.  The todo is deleted and the user redirected to the listing screen to confirm the todo is no longer present.  If the user does not confirm, the process is halted. |
| Requires | A Logged-in User with the Role of Aid |
| Precondition | The Todo exists in the database |
| Postcondition | The Todo no longer exists in the database |
| Side Effects | None |

| ID | CODE-5 |
| --- | --- |
| Function | Todo Properties |
| Description | The valid properties of a Todo |
| Inputs | None |
| Source | None |
| Outputs | A Todo consists of a title (limit: 255 characters, not null, required), a description (limit: 2048 characters, not null, required) and a status (not null, required, see CODE-6, default: Pending). |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |

| ID | CODE-6 |
| --- | --- |
| Function | Valid Statuses |
| Description | The valid status of a Todo |
| Inputs | None |
| Source | None |
| Outputs | Valid statuses for a Todo are Pending, In-Process, Complete and Denied. |
| Requires | None |
| Precondition | None |
| Postcondition | None |
| Side Effects | None |
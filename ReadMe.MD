# Dion's Todo App

This app allows you to Create, Read, Update, and Delete your todo list. You can set a title, priority, due date & time, and description. Tap an item to view its details. Swipe right on an item to delete it. It will give a snackbar with an undo button. Swipe left to open the update screen.

`MainActivity` has a `RecyclerView` ([todo_list_item](app/src/main/res/layout/todo_list_item.xml)). A detail screen opens ([activity_todo_detail](app/src/main/res/layout/activity_todo_detail.xml)) when clicking on a specific todo item. At the screen's bottom-right is a FAB to add a todo item. And at the bottom-right is a FAB to sort the items by date.

All strings and colors reside inside resource files.

`DBHelper` contains all the logic for creating and updating the SQLite database and does not have any logic.

`DBControl` contains methods to insert, update, delete, and select data from the database. It has the most minimal logic possible to keep it as general as possible.

The `Model` class has the most specific information filled in by the extending class. And it has the **best feature**: If a model (something stored in the database) extends the class `Model` and correctly implements its abstract methods, saving and retrieving records is as easy as calling one method. `save()` (update or create), `delete()`, `get()`, and `getAll()`. And all while being fully SQL-injection save!

Every class that extends `Model` should have a Migration class. This contains the SQL to create and delete the table. Additionally, it has the table and column names. In `DBHelper`, these migrations are retrieved and used to set up the database. This way, everything is nicely separated and easily changeable.

## Todo list
- [ ] Properly implement mvvm.
- [ ] Migrate to Kotlin.
- [ ] Add the feature to show a notification at the set due date.

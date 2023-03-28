# ToDo App

This app allows you to Create, Read, Update, and Delete your to-do list.
You can set a title, priority, due date & time, and description.
Tap an item to view their details, and long-press to edit it.

`MainActivity` has a `RecyclerView` ([todo_list_item](app\src\main\res\layout\todo_list_item.xml)). At the bottom of the screen, you can add a to-do item. When saved it is displayed in `MainActivity`. A dialog opens ([todo_detail_dialog](app\src\main\res\layout\todo_detail_dialog.xml)``) when clicking on a specific to-do item.

All strings and colors reside inside resource files.

`DBHelper` contains all the logic for creating and updating the SQLite database and does not have any logic.

`DBControl` contains methods to insert, update, delete, and select data from the database. It has the most minimal logic possible to keep it as general as possible.

The `Model` class has the most specific information filled in by the extending class. And it has the **best feature**: If a model (something stored in the database) extends the class `Model` and correctly implements its abstract methods, saving and retrieving records is as easy as calling one method. `save()` (update & create), `delete()`, `get()`, and `getAll()`. And all while being fully SQL-injection save!

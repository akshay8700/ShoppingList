package com.akshay8700.shoppinglist

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akshay8700.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ShoppingListApp(modifier: Modifier = Modifier) {
    var shoppingListState by remember{ mutableStateOf(listOf<ShoppingListModel>()) }
    var showAlertDialog by remember{ mutableStateOf(false) }
    var itemNameTF by remember{ mutableStateOf("") }
    var itemQuantity by remember{ mutableStateOf("") }

    var context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.padding(24.dp))
        Button(
            onClick = {
                showAlertDialog = true
               Log.i("ShoppingList", "CLicked")
                      },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Add List")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(shoppingListState){myItem ->
                if (myItem.isEditing){
                    ShoppingListEditing(
                        item = myItem,
                        // Pressed on Save button after editing
                        onEditComplete = { editedName, editedQuantity ->
                            shoppingListState = shoppingListState.map { it.copy(isEditing = false) }
                            val editedItem = shoppingListState.find { it.id == myItem.id }
                            editedItem?.let {
                                it.name = editedName
                                it.quantity = editedQuantity
                            }
                        }
                    )
                }
                else {
                    ShoppingListItem(
                        item = myItem,
                        onEditClick = { shoppingListState = shoppingListState.map { it.copy(isEditing = it.id == myItem.id) } },
                        onDeleteClick = { shoppingListState = shoppingListState - myItem }
                    )
                }
            }
        }
    }

    if(showAlertDialog){
        itemQuantity = "1"

        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            confirmButton = {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = {
                        if(itemNameTF.isNotBlank() && itemQuantity.isNotBlank()){
                            val newList = ShoppingListModel(
                                id = shoppingListState.size+1,
                                quantity = itemQuantity.toInt(),
                                name = itemNameTF
                            )
                            // After adding shopping list in newList we will
                            // make AlertDialog text field empty and close it
                            shoppingListState = shoppingListState+newList
                            itemNameTF = ""
                            showAlertDialog = false
                        } else {
                            showToast(context, "Don't let stay anything empty")
                        }
                    }) {
                        Text(text = "Add")
                    }
                    Button(onClick = { showAlertDialog = false }) {
                        Text(text = "Cancel")
                    }
                }
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemNameTF,
                        onValueChange = {itemNameTF = it},
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = {itemQuantity = it},
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            },
            title = { Text(text = "Add item and quantity") }
        )
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingListModel,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
    ) {

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, Color(0XFF018786)),
                shape = RoundedCornerShape(20)
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.name, modifier = Modifier.padding(8.dp))
        Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(8.dp))

        Row(modifier = Modifier.padding(8.dp)) {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}

@Composable
fun ShoppingListEditing(item: ShoppingListModel, onEditComplete: (String, Int) -> Unit) {
    var editedName by remember{ mutableStateOf(item.name) }
    var editedQuantity by remember{ mutableStateOf(item.quantity.toString()) }
    var isEditing by remember{ mutableStateOf(item.isEditing) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(androidx.compose.ui.graphics.Color.White)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column {
            BasicTextField(
                value = editedName, onValueChange = {editedName = it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
            BasicTextField(
                value = editedQuantity, onValueChange = {editedQuantity = it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
            
            Button(
                onClick = { 
                    isEditing = false
                    onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1)
                }
            ) {
                Text(text = "save")
            }
        }
    }
}

data class ShoppingListModel(
    val id: Int,
    var name: String,
    var quantity: Int,
    var isEditing: Boolean = false
)

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Preview(showSystemUi = true)
@Composable
fun Preview() {
    ShoppingListTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ShoppingListApp()
        }
    }
}
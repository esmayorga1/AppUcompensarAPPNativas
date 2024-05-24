package com.example.tiendaucompesarapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException
import java.io.InputStream
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL

data class Producto(
    val nombre: String = "",
    val precio: Double = 0.0,
    val imagenUrl: String = ""
): Serializable



class mainPruductsUcompensar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_pruducts_ucompensar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Boton del carrito general

        val ap = findViewById<TextView>(R.id.textView98)
        ap.setOnClickListener{
            startActivity(intent)
        }

        val cartIcon = findViewById<ImageView>(R.id.cartIcon)

        cartIcon.setOnClickListener {
            // Crear un intent para la actividad cart_products y pasar la lista de productos seleccionados
            val productosList: List<Producto> = productosSeleccionados.toList()
            println(productosList.isEmpty())

            println("la lista: " + ArrayList(productosList))

            val intent = Intent(this, cart_products::class.java)
            intent.putExtra("productos", ArrayList(productosList))
            startActivity(intent)
        }


        // Lista de productos
        val db = FirebaseFirestore.getInstance()
        val productosRef = db.collection("productos")


        // Agregando producto container
        val productoContainer = findViewById<LinearLayout>(R.id.productoContainer)
        productosRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val producto = document.toObject(Producto::class.java)
                    agregarProductoAlContenedor(producto, productoContainer)
                }
            }
            .addOnFailureListener { exception ->
                // Maneja los errores aquí
                Log.e("Firebase", "Error al obtener productos", exception)
                Toast.makeText(this, "Error al obtener productos", Toast.LENGTH_SHORT).show()
            }
    }

    val productosSeleccionados = mutableListOf<Producto>()

    private fun agregarProductoAlContenedor(producto: Producto, contenedor: LinearLayout) {
        val inflater = layoutInflater
        val productoView = inflater.inflate(R.layout.activity_layout_product, contenedor, false)

        val nombreTextView = productoView.findViewById<TextView>(R.id.textView150)
        val precioTextView = productoView.findViewById<TextView>(R.id.textView172)
        val imageView = productoView.findViewById<ImageView>(R.id.imageView111)
        val addCartImageView = productoView.findViewById<ImageView>(R.id.AddCart1)

        nombreTextView.text = producto.nombre
        precioTextView.text = producto.precio.toString()
        loadImageFromUrl(imageView, producto.imagenUrl)

        addCartImageView.setOnClickListener {
            // Mostrar un mensaje con el nombre del producto cuando se hace clic en el botón "AddCart"
            Toast.makeText(this, "El ${producto.nombre} producto agregado al carrito: ", Toast.LENGTH_SHORT).show()
            productosSeleccionados.add(producto)
        }

        contenedor.addView(productoView)
    }

    private fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
        val thread = Thread {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(input)

                // Actualizar la UI en el hilo principal usando un Handler
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    imageView.setImageBitmap(bitmap)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        thread.start()
    }
}

package by.karneichik.DeliveryService

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import by.karneichik.DeliveryService.adapters.ProductListAdapter
import by.karneichik.DeliveryService.viewModels.OrderViewModel
import kotlinx.android.synthetic.main.activity_order_detail.*


class OrderDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: OrderViewModel
    private var adapter: ProductListAdapter = ProductListAdapter(this)
    private val p = Paint()
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1111

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_order_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val uid = intent.getStringExtra(EXTRA_UID_ORDER)!!
        return when (item.itemId) {
            R.id.action_save -> {

                Toast.makeText(this@OrderDetailActivity, R.string.action_save, Toast.LENGTH_SHORT)
                    .show()
                viewModel.saveOrder(uid)
                finish()
                true

            }
            R.id.action_cancel_order -> {
                Toast.makeText(this@OrderDetailActivity, R.string.action_cancel_order, Toast.LENGTH_SHORT)
                    .show()
                viewModel.cancelOrder(uid)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        supportActionBar?.let { it.setDisplayHomeAsUpEnabled(true) }

        if (!intent.hasExtra(EXTRA_UID_ORDER)) {
            finish()
            return
        }
        val uid = intent.getStringExtra(EXTRA_UID_ORDER)!!

        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        viewModel.getOrderInfo(uid).observe(this, Observer {
            with(it) {
                tvClient_FIO.text = client_fio
                tvClient_phone.text = client_phone
                tvClient_phone.setOnClickListener {
                    if (ActivityCompat.checkSelfPermission(
                            this@OrderDetailActivity,
                            Manifest.permission.CALL_PHONE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        AlertDialog.Builder(this@OrderDetailActivity)
                            .setTitle(R.string.dialog_call_ttile)
                            .setMessage(getString(R.string.dialog_call_body).format(client_phone))
                            .setPositiveButton(R.string.yes) { _, _ ->
                                val intentCall = Intent(Intent.ACTION_CALL);
                                intentCall.data = Uri.parse("tel:$client_phone")

                                try {
                                    startActivity(intentCall)
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this@OrderDetailActivity,
                                        "Что-то пошло не так!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            }
                            .setNegativeButton(R.string.no) {_, _ ->

                            }
                            .show()
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this@OrderDetailActivity,
                                Manifest.permission.CALL_PHONE)) {
                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                        } else {
                            // No explanation needed, we can request the permission.
                            ActivityCompat.requestPermissions(this@OrderDetailActivity,
                                arrayOf(Manifest.permission.CALL_PHONE),
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS)

                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    }
                }
                tvAddress.text = address
                tvAddress.setOnClickListener {
                    intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("yandexnavi://map_search").buildUpon().appendQueryParameter("text", address).build()
                    try {
                        startActivity(intent)
                    } catch (e:Exception) {
                        Toast.makeText(this@OrderDetailActivity,"Яндекс Навигатор не установлен",Toast.LENGTH_LONG).show()
                    }

                }
                tvTotalSum.text = totalsum.toString()
                tvPayForm.text = payform
                tvTime.text = time
//                tvNumber.text = number
                supportActionBar?.title = number
            }
        })

//        val adapter = ProductListAdapter(this)
        rvProductList.adapter = adapter

        viewModel.getProductsList(uid).observe(this, Observer {
            adapter.productInfoList = it
        })
//        productsViewModel.getOrderInfo(uid).observe(this, Observer {
//            adapter.productInfoList = it.productsList
//        })

        enableSwipe()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun enableSwipe() {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val product = adapter.getProduct(position)

                    product.delivered = !product.delivered

                    AsyncTask.execute {
                        viewModel.updateProduct(product)
                    }
                    adapter.itemUpdate()


                }


                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {

                    val icon: Bitmap
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                        val itemView = viewHolder.itemView
                        val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                        val width = height / 3
                        val myHolder : ProductListAdapter.ProductViewHolder = viewHolder as ProductListAdapter.ProductViewHolder
                        val isDelivered = myHolder.isDelivered

                        if (isDelivered) {
                            icon = getBitmapFromVectorDrawable( this@OrderDetailActivity, R.drawable.ic_clear_24px)
                            p.color = this@OrderDetailActivity.getColor(R.color.colorCancel)
                        }
                        else {
                            icon = getBitmapFromVectorDrawable(this@OrderDetailActivity, R.drawable.ic_add_24px)
                            p.color = this@OrderDetailActivity.getColor(R.color.colorDelivered)
                        }

                        if (dX > 0 ) {
//                            p.color = this@OrderDetailActivity.getColor(R.color.colorAccent)
                            val background =
                                RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                            c.drawRect(background, p)
//                            icon = getBitmapFromVectorDrawable( this@OrderDetailActivity, R.drawable.ic_clear_24px)
                            val icon_dest = RectF(
                                itemView.left.toFloat() + width,
                                itemView.top.toFloat() + width,
                                itemView.left.toFloat() + 2 * width,
                                itemView.bottom.toFloat() - width
                            )
                            c.drawBitmap(icon, null, icon_dest, p)
                        } else {
//                            p.color = this@OrderDetailActivity.getColor(R.color.colorPrimaryDark)
                            val background = RectF(
                                itemView.right.toFloat() + dX,
                                itemView.top.toFloat(),
                                itemView.right.toFloat(),
                                itemView.bottom.toFloat()
                            )
                            c.drawRect(background, p)
//                            icon = getBitmapFromVectorDrawable(this@OrderDetailActivity, R.drawable.ic_add_24px)
                            val icon_dest = RectF(
                                itemView.right.toFloat() - 2 * width,
                                itemView.top.toFloat() + width,
                                itemView.right.toFloat() - width,
                                itemView.bottom.toFloat() - width
                            )
                            c.drawBitmap(icon, null, icon_dest, p)
                        }
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(rvProductList)
    }

    fun getBitmapFromVectorDrawable(
        context: Context,
        drawableId: Int
    ): Bitmap {
        val drawable =
            ContextCompat.getDrawable(context, drawableId)

        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        with(drawable) {
            setBounds(0, 0, canvas.width, canvas.height)
            draw(canvas)
        }
        return bitmap
    }

    companion object {
        private const val EXTRA_UID_ORDER = "uid"

        fun newIntent(context: Context?, uid: String): Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra(EXTRA_UID_ORDER, uid)
            return intent
        }
    }
}

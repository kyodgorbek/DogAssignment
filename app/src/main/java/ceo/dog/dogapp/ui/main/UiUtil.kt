package ceo.dog.dogapp.ui.main

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.observe
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal fun Fragment.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

internal fun View.gone() {
    visibility = View.GONE
}

internal fun View.visible() {
    visibility = View.VISIBLE
}

internal fun <B : ViewBinding> Fragment.bindingDelegate(
    bindingProvider: (view: View) -> B
): BindingDelegate<B> =
    BindingDelegate(
        this,
        bindingProvider
    )

internal class BindingDelegate<B : ViewBinding>(
    fragment: Fragment,
    private val bindingProvider: (view: View) -> B
) : ReadOnlyProperty<Fragment, B>, LifecycleObserver {

    private var binding: B? = null

    init {
        fragment.viewLifecycleOwnerLiveData.observe(fragment) { it?.lifecycle?.addObserver(this) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    internal fun onDestroyView() {
        binding = null
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): B =
        binding ?: bindingProvider(thisRef.requireView()).apply { binding = this }
}
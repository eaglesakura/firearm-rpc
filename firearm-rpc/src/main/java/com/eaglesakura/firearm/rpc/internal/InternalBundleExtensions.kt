package com.eaglesakura.firearm.rpc.internal

import android.os.Bundle
import android.os.Parcelable
import kotlin.reflect.KProperty

/**
 * Bundle to kotlin delegate.
 *
 * e.g.)
 * val foo: Int by bundle.delegateIntExtra("EXTRA_FOO", 0)
 *
 * fun bar() {
 *      foo = 3 // bundle.putExtra("EXTRA_FOO", 3)
 * }
 *
 * @author @eaglesakura
 * @link https://github.com/eaglesakura/army-knife
 */
internal fun Bundle.delegateIntExtra(key: String, defValue: Int): BundleExtra<Int> =
    BundleIntExtra(this, key, defValue)

/**
 * Bundle to kotlin delegate.
 *
 * e.g.)
 * val foo: Int by bundle.delegateIntExtra("EXTRA_FOO", 0)
 *
 * fun bar() {
 *      foo = 3 // bundle.putExtra("EXTRA_FOO", 3)
 * }
 */
internal fun Bundle.delegateLongExtra(key: String, defValue: Long): BundleExtra<Long> =
    BundleLongExtra(this, key, defValue)

/**
 * Bundle to kotlin delegate.
 *
 * e.g.)
 * val foo: Int by bundle.delegateIntExtra("EXTRA_FOO", 0)
 *
 * fun bar() {
 *      foo = 3 // bundle.putExtra("EXTRA_FOO", 3)
 * }
 */
internal fun Bundle.delegateFloatExtra(key: String, defValue: Float): BundleExtra<Float> =
    BundleFloatExtra(this, key, defValue)

/**
 * Bundle to kotlin delegate.
 *
 * e.g.)
 * val foo: Int by bundle.delegateIntExtra("EXTRA_FOO", 0)
 *
 * fun bar() {
 *      foo = 3 // bundle.putExtra("EXTRA_FOO", 3)
 * }
 */
internal fun Bundle.delegateDoubleExtra(key: String, defValue: Double): BundleExtra<Double> =
    BundleDoubleExtra(this, key, defValue)

/**
 * Bundle to kotlin delegate.
 *
 * e.g.)
 * val foo: Int by bundle.delegateIntExtra("EXTRA_FOO", 0)
 *
 * fun bar() {
 *      foo = 3 // bundle.putExtra("EXTRA_FOO", 3)
 * }
 */
internal fun Bundle.delegateStringExtra(key: String, defValue: String): BundleExtra<String> =
    BundleStringExtra(this, key, defValue)

/**
 * Bundle to kotlin delegate.
 *
 * e.g.)
 * val foo: Int by bundle.delegateIntExtra("EXTRA_FOO", 0)
 *
 * fun bar() {
 *      foo = 3 // bundle.putExtra("EXTRA_FOO", 3)
 * }
 */
internal fun Bundle.delegateByteArrayExtra(key: String): BundleExtra<ByteArray?> =
    BundleByteArrayExtra(this, key)

/**
 * Bundle to kotlin delegate.
 *
 * e.g.)
 * val foo: Int by bundle.delegateIntExtra("EXTRA_FOO", 0)
 *
 * fun bar() {
 *      foo = 3 // bundle.putExtra("EXTRA_FOO", 3)
 * }
 */
internal fun <T : Parcelable> Bundle.delegateParcelableExtra(
    key: String
): BundleExtra<T?> = BundleParcelableExtra(this, key)

/**
 * Bundle to kotlin delegate.
 *
 * e.g.)
 * val foo: Int by bundle.delegateIntExtra("EXTRA_FOO", 0)
 *
 * fun bar() {
 *      foo = 3 // bundle.putExtra("EXTRA_FOO", 3)
 * }
 */
internal fun Bundle.delegateBundleExtra(
    key: String
): BundleExtra<Bundle?> = BundleInBundleExtra(this, key)

/**
 *
 * @author @eaglesakura
 * @link https://github.com/eaglesakura/army-knife
 */
internal interface BundleExtra<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}

internal class BundleIntExtra(
    private val bundle: Bundle,
    private val key: String,
    private val defValue: Int
) :
    BundleExtra<Int> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return bundle.getInt(key, defValue)
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        bundle.putInt(key, value)
    }
}

internal class BundleLongExtra(
    private val bundle: Bundle,
    private val key: String,
    private val defValue: Long
) :
    BundleExtra<Long> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Long {
        return bundle.getLong(key, defValue)
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
        bundle.putLong(key, defValue)
    }
}

internal class BundleFloatExtra(
    private val bundle: Bundle,
    private val key: String,
    private val defValue: Float
) :
    BundleExtra<Float> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Float {
        return bundle.getFloat(key, defValue)
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
        bundle.putFloat(key, defValue)
    }
}

internal class BundleDoubleExtra(
    private val bundle: Bundle,
    private val key: String,
    private val defValue: Double
) :
    BundleExtra<Double> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return bundle.getDouble(key, defValue)
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
        bundle.putDouble(key, value)
    }
}

internal class BundleStringExtra(
    private val bundle: Bundle,
    private val key: String,
    private val defValue: String
) :
    BundleExtra<String> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return bundle.getString(key, defValue)
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        bundle.putString(key, value)
    }
}

internal class BundleByteArrayExtra(
    private val bundle: Bundle,
    private val key: String
) : BundleExtra<ByteArray?> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): ByteArray? {
        return bundle.getByteArray(key)
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: ByteArray?) {
        bundle.putByteArray(key, value)
    }
}

internal class BundleInBundleExtra(
    private val bundle: Bundle,
    private val key: String
) :
    BundleExtra<Bundle?> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Bundle? {
        return bundle.getBundle(key)
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Bundle?) {
        bundle.putBundle(key, value)
    }
}

internal class BundleParcelableExtra<T : Parcelable>(
    private val bundle: Bundle,
    private val key: String
) :
    BundleExtra<T?> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return bundle.getParcelable(key)
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        bundle.putParcelable(key, value)
    }
}
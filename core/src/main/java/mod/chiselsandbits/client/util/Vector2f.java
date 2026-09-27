package mod.chiselsandbits.client.util;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Vector2f {
    public static final Codec<Vector2f> CODEC = Codec.FLOAT.listOf().comapFlatMap((floats) -> Util.fixedSize(floats, 2).map((sizedFloats) -> new Vector2f(sizedFloats.get(0), sizedFloats.get(1))), (vector) -> ImmutableList.of(vector.x, vector.y));
    public static Vector2f XN = new Vector2f(-1.0F, 0.0F);
    public static Vector2f XP = new Vector2f(1.0F, 0.0F);
    public static Vector2f YN = new Vector2f(0.0F, -1.0F);
    public static Vector2f YP = new Vector2f(0.0F, 1.0F);
    public static Vector2f ZERO = new Vector2f(0.0F, 0.0F);
    private float x;
    private float y;

    public Vector2f() {
    }

    public Vector2f(Vector2f vector2f) {
        this.x = vector2f.x;
        this.y = vector2f.y;
    }

    public Vector2f(float a, float b) {
        this.x = a;
        this.y = b;
    }

    public Vector2f(double a, double b) {
        this.x = (float) a;
        this.y = (float) b;
    }

    public Vector2f(Vector4f pVector) {
        this(pVector.x(), pVector.y());
    }

    public Vector2f(Vector3f pVector) {
        this(pVector.x(), pVector.y());
    }

    public Vector2f(Vec3 pVector) {
        this((float)pVector.x, (float)pVector.y);
    }

    public Vector2f(Vec2 pVector) {
        this(pVector.x, pVector.y);
    }

    public boolean equals(Object pOther) {
        if (this == pOther) {
            return true;
        } else if (pOther != null && this.getClass() == pOther.getClass()) {
            Vector2f Vector2f = (Vector2f)pOther;
            if (Float.compare(Vector2f.x, this.x) != 0) {
                return false;
            } else {
                return Float.compare(Vector2f.y, this.y) != 0;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        return 31 * i + Float.floatToIntBits(this.y);
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public void mul(float pMultiplier) {
        this.x *= pMultiplier;
        this.y *= pMultiplier;
    }

    public void mul(Vector2f pMultiplier) {
        this.x *= pMultiplier.x;
        this.y *= pMultiplier.y;
    }

    public void mul(float pMx, float pMy) {
        this.x *= pMx;
        this.y *= pMy;
    }

    public void div(float pMultiplier) {
        this.x /= pMultiplier;
        this.y /= pMultiplier;
    }

    public void div(Vector2f pMultiplier) {
        this.x /= pMultiplier.x;
        this.y /= pMultiplier.y;
    }

    public void div(float pMx, float pMy) {
        this.x /= pMx;
        this.y /= pMy;
    }

    public void clamp(Vector2f pMin, Vector2f pMax) {
        this.x = Mth.clamp(this.x, pMin.x(), pMax.x());
        this.y = Mth.clamp(this.y, pMin.x(), pMax.y());
    }

    public void clamp(float pMin, float pMax) {
        this.x = Mth.clamp(this.x, pMin, pMax);
        this.y = Mth.clamp(this.y, pMin, pMax);
    }

    public void set(float pX, float pY) {
        this.x = pX;
        this.y = pY;
    }

    public void load(Vector2f pOther) {
        this.x = pOther.x;
        this.y = pOther.y;
    }

    public void add(float pX, float pY) {
        this.x += pX;
        this.y += pY;
    }

    public void add(Vector2f pOther) {
        this.x += pOther.x;
        this.y += pOther.y;
    }

    public void sub(Vector2f pOther) {
        this.x -= pOther.x;
        this.y -= pOther.y;
    }

    public float dot(Vector2f pOther) {
        return this.x * pOther.x + this.y * pOther.y;
    }

    public boolean normalize() {
        float f = this.x * this.x + this.y * this.y;
        if ((double)f < 1.0E-5D) {
            return false;
        } else {
            float f1 = (float) Mth.fastInvSqrt(f);
            this.x *= f1;
            this.y *= f1;
            return true;
        }
    }

    public void lerp(Vector2f pVector, float pDelta) {
        float f = 1.0F - pDelta;
        this.x = this.x * f + pVector.x * pDelta;
        this.y = this.y * f + pVector.y * pDelta;
    }

    public Vector2f copy() {
        return new Vector2f(this.x, this.y);
    }

    public void map(Float2FloatFunction pMapper) {
        this.x = pMapper.get(this.x);
        this.y = pMapper.get(this.y);
    }

    public float lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public String toString() {
        return "[" + this.x + ", " + this.y + "]";
    }
}

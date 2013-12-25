package kotlin.nosql

abstract class Op() : Expression {
    fun and(op: Op): Op {
        return AndOp(this, op)
    }

    fun or(op: Op): Op {
        return OrOp(this, op)
    }
}

class IsNullOp(val column: Column<*, *>): Op() {
    override fun toSQL():String {
        return "${Session.get().fullIdentity(column)} IS NULL"
    }
}

class LiteralOp(val value: Any): Op() {
    override fun toSQL():String {
        return if (value is String) "'" + value + "'" else value.toString()
    }
}

class EqualsOp(val expr1: Expression, val expr2: Expression): Op() {
    override fun toSQL():String {
        val sb = StringBuilder()
        if (expr1 is OrOp) {
            sb.append("(").append(expr1.toSQL()).append(")")
        } else {
            sb.append(expr1.toSQL())
        }
        sb.append(" = ")
        if (expr2 is OrOp) {
            sb.append("(").append(expr2.toSQL()).append(")")
        } else {
            sb.append(expr2.toSQL())
        }
        return sb.toString()
    }
}

class LikeOp(val expr1: Expression, val expr2: Expression): Op() {
    override fun toSQL():String {
        val sb = StringBuilder()
        if (expr1 is OrOp) {
            sb.append("(").append(expr1.toSQL()).append(")")
        } else {
            sb.append(expr1.toSQL())
        }
        sb.append(" LIKE ")
        if (expr2 is OrOp) {
            sb.append("(").append(expr2.toSQL()).append(")")
        } else {
            sb.append(expr2.toSQL())
        }
        return sb.toString()
    }
}

class AndOp(val expr1: Expression, val expr2: Expression): Op() {
    override fun toSQL():String {
        val sb = StringBuilder()
        if (expr1 is OrOp) {
            sb.append("(").append(expr1.toSQL()).append(")")
        } else {
            sb.append(expr1.toSQL())
        }
        sb.append(" and ")
        if (expr2 is OrOp) {
            sb.append("(").append(expr2.toSQL()).append(")")
        } else {
            sb.append(expr2.toSQL())
        }
        return sb.toString()
    }
}

class OrOp(val expr1: Expression, val expr2: Expression): Op() {
    override fun toSQL():String {
        return expr1.toSQL() + " or " + expr2.toSQL()
    }
}
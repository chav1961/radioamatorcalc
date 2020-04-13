package chav1961.calc.script;

import java.util.HashMap;
import java.util.Map;

class LocalVarStack implements AutoCloseable {
	enum ValueType {
		INTEGER, REAL, STRING, BOOLEAN; 
		
		static ValueType classify(final Object value) {
			if (value instanceof Boolean) {
				return BOOLEAN;
			}
			else if ((value instanceof Float) || (value instanceof Double)) {
				return REAL;
			}
			else if (value instanceof Number) {
				return INTEGER;
			}
			else if (value instanceof String) {
				return STRING;
			}
			else {
				return null;
			}
		}
	}

	private final LocalVarStack				parent; 
	private final Map<String,TypeAndValue>	locals = new HashMap<>();
	
	public LocalVarStack() {
		this.parent = null;
	}

	private LocalVarStack(final LocalVarStack parent) {
		this.parent = parent;
	}
	
	@Override
	public void close() throws RuntimeException {
		locals.clear();
	}
	
	public boolean add(final String name, final ValueType type) throws IllegalArgumentException, NullPointerException {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name to add can't be null or empty");
		}
		else if (type == null) {
			throw new NullPointerException("Name type can't be null");
		}
		else if (isDefined(name)) {
			return false;
		}
		else {
			locals.put(name, new TypeAndValue(type));
			return true;
		}
	}
	
	public boolean add(final String name, final ValueType type, final Object value) throws IllegalArgumentException, NullPointerException {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name to add can't be null or empty");
		}
		else if (type == null) {
			throw new NullPointerException("Name type can't be null");
		}
		else if (isDefined(name)) {
			return false;
		}
		else {
			add(name,type);
			setValue(name,value);
			return true;
		}
	}

	public boolean exists(final String name) throws IllegalArgumentException {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name to test can't be null or empty");
		}
		else if (isDefined(name)) {
			return true;
		}
		else if (parent == null) {
			return false;
		}
		else {
			return parent.exists(name);
		}
	}

	public boolean isDefined(final String name) throws IllegalArgumentException {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name to test can't be null or empty");
		}
		else {
			return locals.containsKey(name);
		}
	}
	
	public ValueType getType(final String name) throws IllegalArgumentException {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name to get type can't be null or empty");
		}
		else if (isDefined(name)) {
			return locals.get(name).type;
		}
		else if (parent != null) {
			return parent.getType(name);
		}
		else {
			throw new IllegalArgumentException("Name ["+name+"] to get type is not exists"); 
		}
	}
	
	public Object getValue(final String name) throws IllegalArgumentException {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name to get value can't be null or empty");
		}
		else if (isDefined(name)) {
			return locals.get(name).value;
		}
		else if (parent != null) {
			return parent.getValue(name);
		}
		else {
			throw new IllegalArgumentException("Name ["+name+"] to get value is not exists"); 
		}
	}
	
	public void setValue(final String name, Object value) throws IllegalArgumentException {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name to set value can't be null or empty");
		}
		else if (isDefined(name)) {
			final ValueType	valType = ValueType.classify(value); 
			
			if (value == null) {
				locals.get(name).value = value;
			}
			else if (valType == locals.get(name).type) {
				locals.get(name).value = value;
			}
			else if (valType == ValueType.INTEGER && locals.get(name).type == ValueType.REAL) {
				locals.get(name).value = ((Number)value).longValue();
			}
			else if (valType == ValueType.REAL && locals.get(name).type == ValueType.INTEGER) {
				locals.get(name).value = ((Number)value).doubleValue();
			}
			else {
				throw new IllegalArgumentException("Name ["+name+"]: incompatible data type ["+ValueType.classify(value)+"] to assign value to ["+locals.get(name).type+"]"); 
			}
		}
		else if (parent != null) {
			parent.setValue(name,value);
		}
		else {
			throw new IllegalArgumentException("Name ["+name+"] to set value is not exists"); 
		}
	}
	
	public LocalVarStack push() {
		return new LocalVarStack(this);
	}
	
	private static class TypeAndValue {
		final LocalVarStack.ValueType	type;
		Object							value = null;

		public TypeAndValue(final ValueType type) {
			this.type = type;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			TypeAndValue other = (TypeAndValue) obj;
			if (type != other.type) return false;
			if (value == null) {
				if (other.value != null) return false;
			} else if (!value.equals(other.value)) return false;
			return true;
		}

		@Override
		public String toString() {
			return "TypeAndValue [type=" + type + ", value=" + value + "]";
		}
	}
}
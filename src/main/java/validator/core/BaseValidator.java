package validator.core;

public abstract class BaseValidator<T> {

	protected abstract void validateFormat(T dto, ValidationResult result);

	public final ValidationResult validate(T dto) {
		ValidationResult result = new ValidationResult();

		if (dto == null)
			return result;

		validateFormat(dto, result);

		return result;
	}
}

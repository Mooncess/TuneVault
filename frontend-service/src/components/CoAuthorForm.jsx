import { useEffect, useState } from "react";
import styles from "../styles/CoAuthorForm.module.css";

export default function CoAuthorForm({ ownerEmail, onChange }) {
  const [coAuthors, setCoAuthors] = useState([]);
  const [error, setError] = useState("");

  const totalPercentage = coAuthors.reduce(
    (sum, a) => sum + Number(a.percentageOfSale || 0),
    0
  );
  const ownerPercentage = 100 - totalPercentage;

  useEffect(() => {
    if (totalPercentage > 100) {
      setError("Сумма процентов не может превышать 100%");
    } else {
      setError("");
    }

    onChange([
      { email: ownerEmail, percentageOfSale: ownerPercentage },
      ...coAuthors,
    ]);
  }, [coAuthors]);

  const handleAdd = () => {
    setCoAuthors([...coAuthors, { coAuthorEmail: "", percentageOfSale: 0 }]);
  };

  const handleRemove = (index) => {
    const updated = coAuthors.filter((_, i) => i !== index);
    setCoAuthors(updated);
  };

  const handleChange = (index, field, value) => {
    const updated = [...coAuthors];
    updated[index][field] = value;
    setCoAuthors(updated);
  };

  return (
    <div className={styles['coauthors-container']}>
      <div className={`${styles['coauthor-row']} ${styles['owner']}`}>
        <label className={styles['email-label']}>{ownerEmail}</label>
        <label className={styles['percentage-label']}>{ownerPercentage}%</label>
      </div>

      {coAuthors.map((coAuthor, index) => (
        <div className={styles['coauthor-row']} key={index}>
          <input
            className={styles['email-input']}
            type="email"
            placeholder="Email соавтора"
            value={coAuthor.coAuthorEmail}
            onChange={(e) =>
              handleChange(index, "coAuthorEmail", e.target.value)
            }
          />
          <input
            className={styles['percentage-input']}
            type="number"
            min="0"
            max="100"
            value={coAuthor.percentageOfSale}
            onChange={(e) =>
              handleChange(index, "percentageOfSale", e.target.value)
            }
          />
          <button
            type="button"
            className={styles['remove-button']}
            onClick={() => handleRemove(index)}
          >
            −
          </button>
        </div>
      ))}

      <button type="button" className={styles['add-button']} onClick={handleAdd}>
        + Добавить соавтора
      </button>

      {error && <div className={styles['error-message']}>{error}</div>}
    </div>
  );
}

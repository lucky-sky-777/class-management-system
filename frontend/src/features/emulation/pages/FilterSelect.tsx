interface FilterSelectProps {
  label: string;
  val: number | string;
  options: number[] | string[];
  onChange: (value: any) => void;
}

export const FilterSelect = ({ label, val, options, onChange }: FilterSelectProps) => (
  <div className="flex items-center gap-2">
    <span className="text-xs text-ink-3 font-bold uppercase tracking-wider">{label}:</span>
    <select 
      value={val}
      onChange={(e) => onChange(e.target.value)}
      className="text-xs font-bold border-none bg-surface-2 rounded-lg focus:ring-2 focus:ring-ink-blue-fill cursor-pointer py-2 px-3"
    >
      {options.map((opt) => (
        <option key={opt} value={opt}>
          {typeof opt === 'number' && label === 'Tháng' ? `Tháng ${opt}` : opt}
        </option>
      ))}
    </select>
  </div>
);
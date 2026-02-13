import Link from "next/link";

interface CalHeaderProps {
  prevHref: string;
  nextHref: string;
  title: string;
  prevLabel?: string;
  nextLabel?: string;
}

export default function CalHeader({ prevHref, nextHref, title, prevLabel = "Previous", nextLabel = "Next" }: CalHeaderProps) {
  return (
    <div className="cal-header">
      <Link href={prevHref} className="nav-arrow" aria-label={prevLabel}>&lsaquo;</Link>
      <h2>{title}</h2>
      <Link href={nextHref} className="nav-arrow" aria-label={nextLabel}>&rsaquo;</Link>
    </div>
  );
}

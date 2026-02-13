import Link from "next/link";

interface CalHeaderProps {
  prevHref: string;
  nextHref: string;
  title: string;
}

export default function CalHeader({ prevHref, nextHref, title }: CalHeaderProps) {
  return (
    <div className="cal-header">
      <Link href={prevHref} className="nav-arrow">&lsaquo;</Link>
      <h2>{title}</h2>
      <Link href={nextHref} className="nav-arrow">&rsaquo;</Link>
    </div>
  );
}

import { redirect } from "next/navigation";

export const dynamic = "force-dynamic";

export default function Home() {
  const today = new Date();
  redirect(`/month/${today.getFullYear()}/${today.getMonth() + 1}`);
}

import { redirect } from "next/navigation";

export default function Home() {
  const today = new Date();
  redirect(`/month/${today.getFullYear()}/${today.getMonth() + 1}`);
}

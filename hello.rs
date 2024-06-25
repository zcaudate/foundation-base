fn main() {
  println!("Base 10:    {}",69420);
  println!("Base 2:     {:b}",69420);
  println!("Base 8:     {:o}",69420);
  println!("Base 16:    {:x}",69420);
  println!("{number:>5}",number=1);
  println!("{number:0>5}",number=1);
  println!("{number:0<5}",number=1);
  println!("{number:0>width$}",number=1,width=3);
  println!("My name is {0}, {1} {0}","Bond","James");
}
"use client"
 
import * as React from "react"
import * as TabsPrimitive from "@radix-ui/react-tabs"
 
import { cn } from "@/lib/utils"
 
const Tabs = TabsPrimitive.Root
 
const TabsList = React.forwardRef<
  React.ElementRef<typeof TabsPrimitive.List>,
  React.ComponentPropsWithoutRef<typeof TabsPrimitive.List>
>(({ className, ...props }, ref) => (
  <TabsPrimitive.List
    ref={ref}
    className={cn(
      "inline-flex h-10 items-end justify-start gap-0.5 bg-muted/30 px-1",
      className
    )}
    {...props}
  />
))
TabsList.displayName = TabsPrimitive.List.displayName
 
const TabsTrigger = React.forwardRef<
  React.ElementRef<typeof TabsPrimitive.Trigger>,
  React.ComponentPropsWithoutRef<typeof TabsPrimitive.Trigger>
>(({ className, ...props }, ref) => (
  <TabsPrimitive.Trigger
    ref={ref}
    className={cn(
      "group relative inline-flex h-9 items-center justify-center whitespace-nowrap rounded-t-lg px-4 py-2 text-sm font-medium ring-offset-background transition-all duration-150 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-40",
      "data-[state=active]:bg-background data-[state=active]:text-foreground data-[state=active]:shadow-[0_1px_2px_0_rgba(0,0,0,0.05)] data-[state=active]:border-x data-[state=active]:border-t data-[state=active]:border-border/50 data-[state=active]:border-b-transparent",
      "data-[state=inactive]:bg-muted/50 data-[state=inactive]:text-muted-foreground/50 data-[state=inactive]:opacity-60 data-[state=inactive]:border-b data-[state=inactive]:border-border/50 data-[state=inactive]:hover:bg-muted/70 data-[state=inactive]:hover:text-foreground/70 data-[state=inactive]:hover:opacity-80",
      className
    )}
    {...props}
  />
))
TabsTrigger.displayName = TabsPrimitive.Trigger.displayName
 
const TabsContent = React.forwardRef<
  React.ElementRef<typeof TabsPrimitive.Content>,
  React.ComponentPropsWithoutRef<typeof TabsPrimitive.Content>
>(({ className, ...props }, ref) => (
  <TabsPrimitive.Content
    ref={ref}
    className={cn(
      "mt-0 ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 data-[state=active]:animate-in data-[state=active]:fade-in-0 data-[state=active]:zoom-in-95",
      className
    )}
    {...props}
  />
))
TabsContent.displayName = TabsPrimitive.Content.displayName
 
export { Tabs, TabsList, TabsTrigger, TabsContent }

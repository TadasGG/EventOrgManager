export default defineAppConfig({
    ui: {
        button: {
            slots: {
                base: [
                    'hover:brightness-120 rounded-md font-medium inline-flex items-center disabled:cursor-not-allowed aria-disabled:cursor-not-allowed disabled:opacity-75 aria-disabled:opacity-75',
                    'transition-colors'
                ],
                label: 'truncate',
                leadingIcon: 'shrink-0',
                leadingAvatar: 'shrink-0',
                leadingAvatarSize: '',
                trailingIcon: 'shrink-0'
            },
            variants: {
                buttonGroup: {
                    horizontal: 'not-only:first:rounded-e-none not-only:last:rounded-s-none not-last:not-first:rounded-none focus-visible:z-[1]',
                    vertical: 'not-only:first:rounded-b-none not-only:last:rounded-t-none not-last:not-first:rounded-none focus-visible:z-[1]'
                },
                variant: {
                    button: '',
                    selectedButton: '',
                    object: '',
                    icon: ''
                },
                block: {
                    true: {
                        base: 'w-full justify-center',
                        trailingIcon: 'ms-auto'
                    }
                }
            },
            compoundVariants: [
                {
                    variant: 'icon',
                    class: 'text-black'
                },
                {
                    variant: 'button',
                    class: 'button-item aria-disabled:bg-primary focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary'
                },
                {
                    variant: 'selectedButton',
                    class: 'button-item-selected aria-disabled:bg-primary focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary'
                },
                {
                    variant: 'object',
                    class: 'object-item aria-disabled:bg-primary focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary'
                }
            ],
            defaultVariants: {
                color: '',
                variant: 'button',
            }
        },
        input: {
            slots: {
                root: 'relative inline-flex items-center',
                base: [
                    'w-full rounded-md border-0 appearance-none placeholder:text-muted focus:outline-none disabled:cursor-not-allowed disabled:opacity-75',
                    'transition-colors'
                ],
                leading: 'absolute inset-y-0 start-0 flex items-center',
                leadingIcon: 'shrink-0 text-dimmed',
                leadingAvatar: 'shrink-0',
                leadingAvatarSize: '',
                trailing: 'absolute inset-y-0 end-0 flex items-center',
                trailingIcon: 'shrink-0 text-dimmed'
            },
            variants: {
                variant: {
                    input: 'secondary-object-item border focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-primary'
                },
            },
            defaultVariants: {
                color: '',
                variant: 'input'
            }
        },
        separator: {
            slots: {
                root: 'flex items-center align-center text-center',
                border: '',
                container: 'font-medium text-default flex',
                icon: 'shrink-0 size-5',
                avatar: 'shrink-0',
                avatarSize: '2xs',
                label: 'text-sm'
            },
            variants: {
                color: {
                    primarycolor: {
                        border: 'primary-color'
                    },
                    object: {
                        border: 'object-item'
                    },
                    neutral: {
                        border: 'border-default-inverted',
                        container: 'text-black'
                    }
                },
            },
            defaultVariants: {
                color: 'object',
                size: 'md',
                type: 'solid'
            }
        },
        badge: {
            slots: {
                base: 'font-medium inline-flex items-center',
                label: 'truncate',
                leadingIcon: 'shrink-0',
                leadingAvatar: 'shrink-0',
                leadingAvatarSize: '',
                trailingIcon: 'shrink-0'
            },
            variants: {
                buttonGroup: {
                    horizontal: 'not-only:first:rounded-e-none not-only:last:rounded-s-none not-last:not-first:rounded-none focus-visible:z-[1]',
                    vertical: 'not-only:first:rounded-b-none not-only:last:rounded-t-none not-last:not-first:rounded-none focus-visible:z-[1]'
                },
                color: {
                    primary: '',
                    secondary: '',
                    success: '',
                    info: '',
                    warning: '',
                    error: '',
                    neutral: ''
                },
                variant: {
                    solid: '',
                    outline: '',
                    soft: '',
                    subtle: ''
                },
                size: {
                    xs: {
                        base: 'text-[12px]/3 px-1 py-0.5 gap-1 rounded-sm',
                        leadingIcon: 'size-3',
                        leadingAvatarSize: '3xs',
                        trailingIcon: 'size-3'
                    },
                    sm: {
                        base: 'text-[14px]/3 px-1.5 py-1 gap-1 rounded-sm',
                        leadingIcon: 'size-3',
                        leadingAvatarSize: '3xs',
                        trailingIcon: 'size-3'
                    },
                    md: {
                        base: 'text-xs px-2 py-1 gap-1 rounded-md',
                        leadingIcon: 'size-4',
                        leadingAvatarSize: '3xs',
                        trailingIcon: 'size-4'
                    },
                    lg: {
                        base: 'text-sm px-2 py-1 gap-1.5 rounded-md',
                        leadingIcon: 'size-5',
                        leadingAvatarSize: '2xs',
                        trailingIcon: 'size-5'
                    },
                    xl: {
                        base: 'text-base px-2.5 py-1 gap-1.5 rounded-md',
                        leadingIcon: 'size-6',
                        leadingAvatarSize: '2xs',
                        trailingIcon: 'size-6'
                    }
                }
            },
            compoundVariants: [
                {
                    color: 'red',
                    variant: 'solid',
                    class: 'bg-red-400 text-inverted'
                },
                {
                    color: 'teal',
                    variant: 'solid',
                    class: 'bg-teal-400 text-inverted'
                },
                {
                    color: 'blue',
                    variant: 'solid',
                    class: 'bg-blue-400 text-inverted'
                },
                {
                    color: 'violet',
                    variant: 'solid',
                    class: 'bg-violet-400 text-inverted'
                },
                {
                    color: 'lime',
                    variant: 'solid',
                    class: 'bg-lime-400 text-inverted'
                },
                {
                    color: 'amber',
                    variant: 'solid',
                    class: 'bg-amber-400 text-inverted'
                },
                {
                    color: 'gray',
                    variant: 'solid',
                    class: 'bg-gray-400 text-inverted'
                }
            ],
            defaultVariants: {
                color: 'gray',
            }
        },
        textarea: {
            slots: {
                root: 'relative inline-flex items-center',
                base: [
                    'w-full rounded-md border-0 appearance-none placeholder:text-toned focus:outline-none disabled:cursor-not-allowed disabled:opacity-75 disabled',
                    'transition-colors'
                ],
                leading: 'absolute start-0 flex items-start',
                leadingIcon: 'shrink-0 text-dimmed',
                leadingAvatar: 'shrink-0',
                leadingAvatarSize: '',
                trailing: 'absolute end-0 flex items-start',
                trailingIcon: 'shrink-0 text-dimmed'
            },
            variants: {
                variant: {
                    input: ''
                },
            },
            compoundVariants: [
                {
                    variant: 'input',
                    class: 'secondary-object-item border focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-primary'
                }
            ],
            defaultVariants: {
                color: '',
                variant: 'input'
            }
        },
        select: {
            slots: {
                base: [
                    'relative group rounded-md inline-flex items-center focus:outline-none disabled:cursor-not-allowed disabled:opacity-75',
                    'transition-colors'
                ],
                leading: 'absolute inset-y-0 start-0 flex items-center',
                leadingIcon: 'shrink-0 text-dimmed',
                leadingAvatar: 'shrink-0',
                leadingAvatarSize: '',
                trailing: 'absolute inset-y-0 end-0 flex items-center',
                trailingIcon: 'shrink-0 text-dimmed',
                value: 'truncate pointer-events-none',
                placeholder: 'truncate text-toned',
                arrow: 'fill-default',
                content: 'max-h-60 w-(--reka-select-trigger-width) bg-default shadow-lg rounded-md ring ring-default overflow-hidden data-[state=open]:animate-[scale-in_100ms_ease-out] data-[state=closed]:animate-[scale-out_100ms_ease-in] origin-(--reka-select-content-transform-origin) pointer-events-auto flex flex-col',
                viewport: 'relative divide-y divide-default scroll-py-1 overflow-y-auto flex-1',
                group: 'p-1 isolate',
                empty: 'text-center text-muted',
                label: 'font-semibold text-highlighted',
                separator: '-mx-1 my-1 h-px bg-border',
                item: [
                    'group relative w-full flex items-center select-none outline-none before:absolute before:z-[-1] before:inset-px before:rounded-md data-disabled:cursor-not-allowed data-disabled:opacity-75 text-default data-highlighted:not-data-disabled:text-highlighted data-highlighted:not-data-disabled:before:bg-elevated/50',
                    'transition-colors before:transition-colors'
                ],
                itemLeadingAvatar: 'shrink-0',
                itemLeadingAvatarSize: '',
                itemLeadingChip: 'shrink-0',
                itemLeadingChipSize: '',
                itemTrailing: 'ms-auto inline-flex gap-1.5 items-center',
                itemTrailingIcon: 'shrink-0',
                itemLabel: 'truncate'
            },
            variants: {
                variant: {
                    input: 'secondary-object-item border',
                }
            },
            compoundVariants: [
                {
                    color: 'primary',
                    variant: [
                        'outline',
                        'subtle'
                    ],
                    class: 'focus:ring-2 focus:ring-inset focus:ring-primary'
                },
                {
                    color: 'primary',
                    highlight: true,
                    class: 'ring ring-inset ring-primary'
                },
                {
                    color: 'neutral',
                    variant: [
                        'outline',
                        'subtle'
                    ],
                    class: 'focus:ring-2 focus:ring-inset focus:ring-inverted'
                },
                {
                    color: 'neutral',
                    highlight: true,
                    class: 'ring ring-inset ring-inverted'
                },
                {
                    leading: true,
                    size: 'xs',
                    class: 'ps-7'
                },
                {
                    leading: true,
                    size: 'sm',
                    class: 'ps-8'
                },
                {
                    leading: true,
                    size: 'md',
                    class: 'ps-9'
                },
                {
                    leading: true,
                    size: 'lg',
                    class: 'ps-10'
                },
                {
                    leading: true,
                    size: 'xl',
                    class: 'ps-11'
                },
                {
                    trailing: true,
                    size: 'xs',
                    class: 'pe-7'
                },
                {
                    trailing: true,
                    size: 'sm',
                    class: 'pe-8'
                },
                {
                    trailing: true,
                    size: 'md',
                    class: 'pe-9'
                },
                {
                    trailing: true,
                    size: 'lg',
                    class: 'pe-10'
                },
                {
                    trailing: true,
                    size: 'xl',
                    class: 'pe-11'
                },
                {
                    loading: true,
                    leading: true,
                    class: {
                        leadingIcon: 'animate-spin'
                    }
                },
                {
                    loading: true,
                    leading: false,
                    trailing: true,
                    class: {
                        trailingIcon: 'animate-spin'
                    }
                }
            ],
            defaultVariants: {
                color: '',
                variant: 'input'
            }
        },
        tabs: {
            slots: {
                root: 'flex items-center gap-2',
                list: 'relative flex p-1 group',
                indicator: 'absolute transition-[translate,width] duration-200',
                trigger: [
                    'group relative inline-flex items-center min-w-0 data-[state=inactive]:text-muted hover:data-[state=inactive]:not-disabled:text-default font-medium rounded-md disabled:cursor-not-allowed disabled:opacity-75',
                    'transition-colors'
                ],
                leadingIcon: 'shrink-0',
                leadingAvatar: 'shrink-0',
                leadingAvatarSize: '',
                label: 'truncate',
                trailingBadge: 'shrink-0',
                trailingBadgeSize: 'sm',
                content: 'focus:outline-none w-full'
            },
            variants: {
                variant: {
                    pill: {
                        list: 'bg-elevated secondary-object-item rounded-lg',
                        trigger: 'grow',
                        indicator: 'rounded-md shadow-xs'
                    }
                },
            },
            compoundVariants: [
                {
                    color: 'primary',
                    variant: 'pill',
                    class: {
                        indicator: 'button-item',
                        trigger: 'data-[state=active]:text-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary'
                    }
                }
            ],
            defaultVariants: {
                color: 'primary',
                variant: 'pill'
            }
        },
        inputNumber: {
            slots: {
                root: 'relative inline-flex items-center',
                base: [
                    'secondary-object-item w-full rounded-md border placeholder:text-muted focus:outline-none disabled:cursor-not-allowed disabled:opacity-75',
                    'transition-colors'
                ],
                increment: 'absolute flex items-center',
                decrement: 'absolute flex items-center'
            }
        },
        navigationMenu: {
            slots: {
                root: 'relative flex gap-1.5 [&>div]:min-w-0',
                list: 'isolate min-w-0',
                label: 'w-full flex items-center gap-1.5 font-semibold text-xs/5 text-highlighted px-2.5 py-1.5',
                item: 'min-w-0',
                link: 'group relative w-full flex items-center gap-1.5 font-medium text-sm before:absolute before:z-[-1] before:rounded-md focus:outline-none focus-visible:outline-none dark:focus-visible:outline-none focus-visible:before:ring-inset focus-visible:before:ring-2',
                linkLeadingIcon: 'shrink-0 size-5 bg-white',
                linkLeadingAvatar: 'shrink-0',
                linkLeadingAvatarSize: '2xs',
                linkTrailing: 'group ms-auto inline-flex gap-1.5 items-center text-[var(--text-color)]',
                linkTrailingBadge: 'shrink-0',
                linkTrailingBadgeSize: 'sm',
                linkTrailingIcon: 'size-5 transform shrink-0 group-data-[state=open]:rotate-180 transition-transform duration-200',
                linkLabel: 'truncate',
                linkLabelExternalIcon: 'inline-block size-3 align-top text-dimmed',
                childList: 'isolate',
                childLabel: 'text-xs text-highlighted',
                childItem: '',
                childLink: 'group relative size-full flex items-start text-start text-sm before:absolute before:z-[-1] before:rounded-md focus:outline-none focus-visible:outline-none dark:focus-visible:outline-none focus-visible:before:ring-inset focus-visible:before:ring-2',
                childLinkWrapper: 'min-w-0',
                childLinkIcon: 'size-5 shrink-0',
                childLinkLabel: 'truncate',
                childLinkLabelExternalIcon: 'inline-block size-3 align-top text-dimmed',
                childLinkDescription: 'text-muted',
                separator: 'px-2 h-px bg-border',
                viewportWrapper: 'absolute top-full left-0 flex w-full',
                viewport: 'relative overflow-hidden bg-default shadow-lg rounded-md ring ring-default h-(--reka-navigation-menu-viewport-height) w-full transition-[width,height,left] duration-200 origin-[top_center] data-[state=open]:animate-[scale-in_100ms_ease-out] data-[state=closed]:animate-[scale-out_100ms_ease-in] z-[1]',
                content: '',
                indicator: 'absolute data-[state=visible]:animate-[fade-in_100ms_ease-out] data-[state=hidden]:animate-[fade-out_100ms_ease-in] data-[state=hidden]:opacity-0 bottom-0 z-[2] w-(--reka-navigation-menu-indicator-size) translate-x-(--reka-navigation-menu-indicator-position) flex h-2.5 items-end justify-center overflow-hidden transition-[translate,width] duration-200',
                arrow: 'relative top-[50%] size-2.5 rotate-45 border border-default bg-default z-[1] rounded-xs'
            },
            variants: {
                color: {
                    main: {
                        link: 'focus-visible:before:ring-primary',
                        childLink: 'focus-visible:before:ring-primary'
                    },
                    secondary: {
                        link: 'focus-visible:before:ring-secondary',
                        childLink: 'focus-visible:before:ring-secondary'
                    },
                    success: {
                        link: 'focus-visible:before:ring-success',
                        childLink: 'focus-visible:before:ring-success'
                    },
                    info: {
                        link: 'focus-visible:before:ring-info',
                        childLink: 'focus-visible:before:ring-info'
                    },
                    warning: {
                        link: 'focus-visible:before:ring-warning',
                        childLink: 'focus-visible:before:ring-warning'
                    },
                    error: {
                        link: 'focus-visible:before:ring-error',
                        childLink: 'focus-visible:before:ring-error'
                    },
                    neutral: {
                        link: 'focus-visible:before:ring-inverted',
                        childLink: 'focus-visible:before:ring-inverted'
                    }
                },
                highlightColor: {
                    main: '',
                    secondary: '',
                    success: '',
                    info: '',
                    warning: '',
                    error: '',
                    neutral: ''
                },
                variant: {
                    pill: '',
                    link: ''
                },
                active: {
                    true: {
                        link: 'text-[var(--text-color)] secondary-color rounded',
                        linkLeadingIcon: 'bg-[var(--text-color)]',
                        childLink: 'before:bg-elevated text-highlighted',
                        childLinkIcon: 'text-default'
                    },
                    false: {
                        link: 'text-[var(--text-color)]',
                        linkLeadingIcon: 'bg-[var(--text-color)]',
                        childLink: [
                            'hover:before:bg-elevated/50 text-default hover:text-highlighted',
                            'transition-colors before:transition-colors'
                        ],
                        childLinkIcon: [
                            'text-dimmed group-hover:text-default',
                            'transition-colors'
                        ]
                    }
                }
            },
            compoundVariants: [
                {
                    orientation: 'horizontal',
                    contentOrientation: 'horizontal',
                    class: {
                        childList: 'grid-cols-2 gap-2'
                    }
                },
                {
                    orientation: 'horizontal',
                    contentOrientation: 'vertical',
                    class: {
                        childList: 'gap-1',
                        content: 'w-60'
                    }
                },
                {
                    orientation: 'vertical',
                    collapsed: false,
                    class: {
                        childList: 'ms-5 border-s border-[var(--text-color)]',
                        childItem: 'ps-1.5 -ms-px',
                        content: 'data-[state=open]:animate-[collapsible-down_200ms_ease-out] data-[state=closed]:animate-[collapsible-up_200ms_ease-out] overflow-hidden'
                    }
                },
                {
                    orientation: 'vertical',
                    collapsed: true,
                    class: {
                        link: 'px-1.5',
                        content: 'shadow-sm rounded-sm min-h-6 p-1'
                    }
                },
                {
                    orientation: 'vertical',
                    highlight: true,
                    level: true,
                    class: {
                        link: [
                            'after:absolute after:-start-1.5 after:inset-y-0.5 after:block after:w-px after:rounded-full',
                            'after:transition-colors'
                        ]
                    }
                },
                {
                    disabled: false,
                    active: false,
                    variant: 'pill',
                    class: {
                        link: [
                            'hover:text-highlighted hover:before:bg-elevated/50',
                            'transition-colors before:transition-colors'
                        ],
                        linkLeadingIcon: [
                            'group-hover:text-default',
                            'transition-colors'
                        ]
                    }
                },
                {
                    color: 'primary',
                    variant: 'pill',
                    active: true,
                    class: {
                        link: 'text-primary',
                        linkLeadingIcon: 'text-primary group-data-[state=open]:text-primary'
                    }
                },
                {
                    color: 'neutral',
                    variant: 'pill',
                    active: true,
                    class: {
                        link: 'text-highlighted',
                        linkLeadingIcon: 'text-highlighted group-data-[state=open]:text-highlighted'
                    }
                },
                {
                    variant: 'pill',
                    active: true,
                    highlight: false,
                    class: {
                        link: 'before:bg-elevated'
                    }
                },
                {
                    variant: 'pill',
                    active: true,
                    highlight: true,
                    disabled: false,
                    class: {
                        link: [
                            'hover:before:bg-elevated/50',
                            'before:transition-colors'
                        ]
                    }
                },
                {
                    disabled: false,
                    active: false,
                    variant: 'link',
                    class: {
                        link: [
                            'hover:text-highlighted',
                            'transition-colors'
                        ],
                        linkLeadingIcon: [
                            'group-hover:text-default',
                            'transition-colors'
                        ]
                    }
                },
                {
                    disabled: false,
                    active: false,
                    variant: 'link',
                    orientation: 'horizontal',
                    class: {
                        link: 'data-[state=open]:text-highlighted',
                        linkLeadingIcon: 'group-data-[state=open]:text-default'
                    }
                },
                {
                    color: 'primary',
                    variant: 'link',
                    active: true,
                    class: {
                        link: 'text-primary',
                        linkLeadingIcon: 'text-primary group-data-[state=open]:text-primary'
                    }
                },
                {
                    highlightColor: 'primary',
                    highlight: true,
                    level: true,
                    active: true,
                    class: {
                        link: 'after:bg-primary'
                    }
                }
            ],
            defaultVariants: {
                color: 'main',
                highlightColor: 'main',
                variant: 'pill'
            }
        },
        formField: {
            slots: {
                root: '',
                wrapper: '',
                labelWrapper: 'flex content-center items-center justify-between',
                label: 'block font-medium text-base text-highlighted',
                container: 'mt-1 relative',
                description: 'text-muted',
                error: 'mt-2 text-error',
                hint: 'text-muted',
                help: 'mt-2 text-muted'
            },
            variants: {
                required: {
                    true: {
                        label: "after:content-['*'] after:ms-0.5 after:text-error"
                    }
                }
            }
        },
        chip: {
            slots: {
                root: 'relative inline-flex items-center justify-center shrink-0',
                base: 'rounded-full ring-0 ring-bg flex items-center justify-center text-inverted font-medium whitespace-nowrap'
            },
            variants: {
                color: {
                    red: 'bg-red-400 text-inverted',
                    teal: 'bg-teal-400 text-inverted',
                    blue: 'bg-blue-400 text-inverted',
                    violet: 'bg-violet-400 text-inverted',
                    lime: 'bg-lime-400 text-inverted',
                    amber: 'bg-amber-400 text-inverted',
                    gray: 'bg-gray-400 text-inverted',
                    none: 'bg-none'
                },
                size: {
                    '3xs': 'h-[4px] min-w-[4px] text-[4px]',
                    '2xs': 'h-[5px] min-w-[5px] text-[5px]',
                    xs: 'h-[6px] min-w-[6px] text-[6px]',
                    sm: 'h-[7px] min-w-[7px] text-[7px]',
                    md: 'h-[8px] min-w-[8px] text-[8px]',
                    lg: 'h-[9px] min-w-[9px] text-[9px]',
                    xl: 'h-[10px] min-w-[10px] text-[10px]',
                    '2xl': 'h-[11px] min-w-[11px] text-[11px]',
                    '3xl': 'h-[12px] min-w-[12px] text-[12px]'
                },
                position: {
                    'top-right': 'top-0 right-0',
                    'bottom-right': 'bottom-0 right-0',
                    'top-left': 'top-0 left-0',
                    'bottom-left': 'bottom-0 left-0'
                },
                inset: {
                    false: ''
                },
                standalone: {
                    false: 'absolute'
                }
            },
            compoundVariants: [
                {
                    position: 'top-right',
                    inset: false,
                    class: '-translate-y-1/2 translate-x-1/2 transform'
                },
                {
                    position: 'bottom-right',
                    inset: false,
                    class: 'translate-y-1/2 translate-x-1/2 transform'
                },
                {
                    position: 'top-left',
                    inset: false,
                    class: '-translate-y-1/2 -translate-x-1/2 transform'
                },
                {
                    position: 'bottom-left',
                    inset: false,
                    class: 'translate-y-1/2 -translate-x-1/2 transform'
                }
            ],
            defaultVariants: {
                size: 'md',
                color: 'primary',
                position: 'top-right'
            }
        },
        table: {
            slots: {
                root: 'relative overflow-auto border-2 border-[var(--primary-color)] bg-[var(--object-color)] shadow-md',
                base: 'min-w-full overflow-clip',
                caption: 'sr-only',
                thead: 'relative bg-[var(--primary-color)]',
                tbody: 'divide-y [&>tr]:border-[var(--primary-color)] [&>tr]:border-t-1 [&>tr]:data-[selectable=true]:hover:bg-elevated/50 [&>tr]:data-[selectable=true]:focus-visible:outline-primary',
                tfoot: 'relative',
                tr: 'data-[selected=true]:bg-elevated/50',
                th: 'whitespace-nowrap px-4 py-2 text-sm text-highlighted text-left rtl:text-right font-bold [&:has([role=checkbox])]:pe-0',
                td: 'p-4 text-sm text-highlighted whitespace-nowrap [&:has([role=checkbox])]:pe-0',
                separator: 'absolute z-[1] left-0 w-full primary-color h-[2px]',
                empty: 'py-6 text-center text-sm text-highlighted',
                loading: 'py-6 text-center'
            },
            variants: {
                pinned: {
                    true: {
                        th: 'sticky bg-default/75 data-[pinned=left]:left-0 data-[pinned=right]:right-0',
                        td: 'sticky bg-default/75 data-[pinned=left]:left-0 data-[pinned=right]:right-0'
                    }
                },
                sticky: {
                    true: {
                        thead: 'sticky top-0 inset-x-5 bg-default/75 z-[1] backdrop-blur',
                        tfoot: 'sticky bottom-0 inset-x-0 bg-default/75 z-[1] backdrop-blur'
                    },
                    header: {
                        thead: 'sticky top-0 inset-x-0 bg-default/75 z-[1] backdrop-blur'
                    },
                    footer: {
                        tfoot: 'sticky bottom-0 inset-x-0 bg-default/75 z-[1] backdrop-blur'
                    }
                },
                loadingColor: {
                    primary: '',
                    secondary: '',
                    success: '',
                    info: '',
                    warning: '',
                    error: '',
                    neutral: ''
                }
            },
            compoundVariants: [
                {
                    loading: true,
                    loadingColor: 'primary',
                    class: {
                        thead: 'after:bg-red-400'
                    }
                },
                {
                    loading: true,
                    loadingColor: 'neutral',
                    class: {
                        thead: 'after:bg-inverted'
                    }
                },
                {
                    loading: true,
                    loadingAnimation: 'carousel',
                    class: {
                        thead: 'after:animate-[carousel_2s_ease-in-out_infinite] rtl:after:animate-[carousel-rtl_2s_ease-in-out_infinite]'
                    }
                },
                {
                    loading: true,
                    loadingAnimation: 'carousel-inverse',
                    class: {
                        thead: 'after:animate-[carousel-inverse_2s_ease-in-out_infinite] rtl:after:animate-[carousel-inverse-rtl_2s_ease-in-out_infinite]'
                    }
                },
                {
                    loading: true,
                    loadingAnimation: 'swing',
                    class: {
                        thead: 'after:animate-[swing_2s_ease-in-out_infinite]'
                    }
                },
                {
                    loading: true,
                    loadingAnimation: 'elastic',
                    class: {
                        thead: 'after:animate-[elastic_2s_ease-in-out_infinite]'
                    }
                }
            ],
            defaultVariants: {
                loadingColor: 'primary',
                loadingAnimation: 'carousel'
            }
        },
        switch: {
            slots: {
                root: 'relative flex items-start',
                base: [
                    'inline-flex items-center shrink-0 rounded-full border-2 border-transparent focus-visible:outline-2 focus-visible:outline-offset-2 data-[state=unchecked]:bg-accented',
                    'transition-[background] duration-200'
                ],
                container: 'flex items-center',
                thumb: 'group pointer-events-none rounded-full bg-default shadow-lg ring-0 transition-transform duration-200 data-[state=unchecked]:translate-x-0 data-[state=unchecked]:rtl:-translate-x-0 flex items-center justify-center',
                icon: [
                    'absolute shrink-0 opacity-0 size-10/12',
                    'transition-[color,opacity] duration-200'
                ],
                wrapper: 'ms-2',
                label: 'block font-medium text-default',
                description: 'text-muted'
            },
            variants: {
                color: {
                    primary: {
                        base: 'data-[state=checked]:bg-primary focus-visible:outline-primary',
                        icon: 'group-data-[state=checked]:text-primary'
                    },
                    secondary: {
                        base: 'data-[state=checked]:bg-secondary focus-visible:outline-secondary',
                        icon: 'group-data-[state=checked]:text-secondary'
                    },
                    success: {
                        base: 'data-[state=checked]:bg-success focus-visible:outline-success',
                        icon: 'group-data-[state=checked]:text-success'
                    },
                    info: {
                        base: 'data-[state=checked]:bg-info focus-visible:outline-info',
                        icon: 'group-data-[state=checked]:text-info'
                    },
                    warning: {
                        base: 'data-[state=checked]:bg-warning focus-visible:outline-warning',
                        icon: 'group-data-[state=checked]:text-warning'
                    },
                    error: {
                        base: 'data-[state=checked]:bg-error focus-visible:outline-error',
                        icon: 'group-data-[state=checked]:text-error'
                    },
                    neutral: {
                        base: 'data-[state=checked]:bg-inverted focus-visible:outline-inverted',
                        icon: 'group-data-[state=checked]:text-highlighted'
                    }
                },
                size: {
                    xs: {
                        base: 'w-7',
                        container: 'h-4',
                        thumb: 'size-3 data-[state=checked]:translate-x-3 data-[state=checked]:rtl:-translate-x-3',
                        wrapper: 'text-xs'
                    },
                    sm: {
                        base: 'w-8',
                        container: 'h-4',
                        thumb: 'size-3.5 data-[state=checked]:translate-x-3.5 data-[state=checked]:rtl:-translate-x-3.5',
                        wrapper: 'text-xs'
                    },
                    md: {
                        base: 'w-9',
                        container: 'h-5',
                        thumb: 'size-4 data-[state=checked]:translate-x-4 data-[state=checked]:rtl:-translate-x-4',
                        wrapper: 'text-sm'
                    },
                    lg: {
                        base: 'w-10',
                        container: 'h-5',
                        thumb: 'size-4.5 data-[state=checked]:translate-x-4.5 data-[state=checked]:rtl:-translate-x-4.5',
                        wrapper: 'text-sm'
                    },
                    xl: {
                        base: 'w-11',
                        container: 'h-6',
                        thumb: 'size-5 data-[state=checked]:translate-x-5 data-[state=checked]:rtl:-translate-x-5',
                        wrapper: 'text-base'
                    }
                },
                checked: {
                    true: {
                        icon: 'group-data-[state=checked]:opacity-100'
                    }
                },
                unchecked: {
                    true: {
                        icon: 'group-data-[state=unchecked]:opacity-100'
                    }
                },
                loading: {
                    true: {
                        icon: 'animate-spin'
                    }
                },
                required: {
                    true: {
                        label: "after:content-['*'] after:ms-0.5 after:text-error"
                    }
                },
                disabled: {
                    true: {
                        base: 'cursor-not-allowed opacity-75',
                        label: 'cursor-not-allowed opacity-75',
                        description: 'cursor-not-allowed opacity-75'
                    }
                }
            },
            defaultVariants: {
                color: 'primary',
                size: 'md'
            }
        },
        selectMenu: {
            slots: {
                base: [
                    'relative group rounded-md inline-flex items-center focus:outline-none disabled:cursor-not-allowed disabled:opacity-75',
                    'transition-colors'
                ],
                leading: 'absolute inset-y-0 start-0 flex items-center',
                leadingIcon: 'shrink-0 text-dimmed',
                leadingAvatar: 'shrink-0',
                leadingAvatarSize: '',
                trailing: 'absolute inset-y-0 end-0 flex items-center',
                trailingIcon: 'shrink-0 text-dimmed',
                value: 'truncate pointer-events-none',
                placeholder: 'truncate text-muted',
                arrow: 'fill-default',
                content: [
                    'max-h-60 w-(--reka-select-trigger-width) bg-[var(--primary-color)] shadow-lg rounded-md ring ring-[var(--background-color)] overflow-hidden data-[state=open]:animate-[scale-in_100ms_ease-out] data-[state=closed]:animate-[scale-out_100ms_ease-in] origin-(--reka-select-content-transform-origin) pointer-events-auto flex flex-col',
                    'origin-(--reka-combobox-content-transform-origin) w-(--reka-combobox-trigger-width)'
                ],
                viewport: 'bg-[var(--object-color)] relative divide-y divide-default scroll-py-1 overflow-y-auto flex-1',
                group: 'p-1 isolate',
                empty: 'text-center text-muted',
                label: 'font-semibold text-highlighted',
                separator: '-mx-1 my-1 h-px bg-border',
                item: [
                    'group relative w-full flex items-center select-none outline-none before:absolute before:z-[-1] before:inset-px before:rounded-md data-disabled:cursor-not-allowed data-disabled:opacity-75 text-default data-highlighted:not-data-disabled:text-highlighted data-highlighted:not-data-disabled:before:bg-elevated/50',
                    'transition-colors before:transition-colors'
                ],
                itemLeadingIcon: [
                    'shrink-0 text-dimmed group-data-highlighted:not-group-data-disabled:text-default',
                    'transition-colors'
                ],
                itemLeadingAvatar: 'shrink-0',
                itemLeadingAvatarSize: '',
                itemLeadingChip: 'shrink-0',
                itemLeadingChipSize: '',
                itemTrailing: 'ms-auto inline-flex gap-1.5 items-center',
                itemTrailingIcon: 'shrink-0',
                itemLabel: 'truncate',
                input: 'border-b border-default',
                focusScope: 'flex flex-col min-h-0'
            },
            variants: {
                buttonGroup: {
                    horizontal: 'not-only:first:rounded-e-none not-only:last:rounded-s-none not-last:not-first:rounded-none focus-visible:z-[1]',
                    vertical: 'not-only:first:rounded-b-none not-only:last:rounded-t-none not-last:not-first:rounded-none focus-visible:z-[1]'
                },
                size: {
                    xs: {
                        base: 'px-2 py-1 text-xs gap-1',
                        leading: 'ps-2',
                        trailing: 'pe-2',
                        leadingIcon: 'size-4',
                        leadingAvatarSize: '3xs',
                        trailingIcon: 'size-4',
                        label: 'p-1 text-[10px]/3 gap-1',
                        item: 'p-1 text-xs gap-1',
                        itemLeadingIcon: 'size-4',
                        itemLeadingAvatarSize: '3xs',
                        itemLeadingChip: 'size-4',
                        itemLeadingChipSize: 'sm',
                        itemTrailingIcon: 'size-4',
                        empty: 'p-1 text-xs'
                    },
                    sm: {
                        base: 'px-2.5 py-1.5 text-xs gap-1.5',
                        leading: 'ps-2.5',
                        trailing: 'pe-2.5',
                        leadingIcon: 'size-4',
                        leadingAvatarSize: '3xs',
                        trailingIcon: 'size-4',
                        label: 'p-1.5 text-[10px]/3 gap-1.5',
                        item: 'p-1.5 text-xs gap-1.5',
                        itemLeadingIcon: 'size-4',
                        itemLeadingAvatarSize: '3xs',
                        itemLeadingChip: 'size-4',
                        itemLeadingChipSize: 'sm',
                        itemTrailingIcon: 'size-4',
                        empty: 'p-1.5 text-xs'
                    },
                    md: {
                        base: 'px-2.5 py-1.5 text-sm gap-1.5',
                        leading: 'ps-2.5',
                        trailing: 'pe-2.5',
                        leadingIcon: 'size-5',
                        leadingAvatarSize: '2xs',
                        trailingIcon: 'size-5',
                        label: 'p-1.5 text-xs gap-1.5',
                        item: 'p-1.5 text-sm gap-1.5',
                        itemLeadingIcon: 'size-5',
                        itemLeadingAvatarSize: '2xs',
                        itemLeadingChip: 'size-5',
                        itemLeadingChipSize: 'md',
                        itemTrailingIcon: 'size-5',
                        empty: 'p-1.5 text-sm'
                    },
                    lg: {
                        base: 'px-3 py-2 text-sm gap-2',
                        leading: 'ps-3',
                        trailing: 'pe-3',
                        leadingIcon: 'size-5',
                        leadingAvatarSize: '2xs',
                        trailingIcon: 'size-5',
                        label: 'p-2 text-xs gap-2',
                        item: 'p-2 text-sm gap-2',
                        itemLeadingIcon: 'size-5',
                        itemLeadingAvatarSize: '2xs',
                        itemLeadingChip: 'size-5',
                        itemLeadingChipSize: 'md',
                        itemTrailingIcon: 'size-5',
                        empty: 'p-2 text-sm'
                    },
                    xl: {
                        base: 'px-3 py-2 text-base gap-2',
                        leading: 'ps-3',
                        trailing: 'pe-3',
                        leadingIcon: 'size-6',
                        leadingAvatarSize: 'xs',
                        trailingIcon: 'size-6',
                        label: 'p-2 text-sm gap-2',
                        item: 'p-2 text-base gap-2',
                        itemLeadingIcon: 'size-6',
                        itemLeadingAvatarSize: 'xs',
                        itemLeadingChip: 'size-6',
                        itemLeadingChipSize: 'lg',
                        itemTrailingIcon: 'size-6',
                        empty: 'p-2 text-base'
                    }
                },
                variant: {
                    outline: 'ring-0 secondary-object-item border focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-primary',
                },
                color: {
                    primary: '',
                    secondary: '',
                    success: '',
                    info: '',
                    warning: '',
                    error: '',
                    neutral: ''
                },
                leading: {
                    true: ''
                },
                trailing: {
                    true: ''
                },
                loading: {
                    true: ''
                },
                highlight: {
                    true: ''
                },
                type: {
                    file: 'file:me-1.5 file:font-medium file:text-muted file:outline-none'
                }
            },
            compoundVariants: [
                {
                    color: 'primary',
                    variant: [
                        'outline',
                        'subtle'
                    ],
                    class: ''
                },
                {
                    color: 'primary',
                    highlight: true,
                    class: 'ring ring-inset ring-primary'
                },
                {
                    color: 'neutral',
                    variant: [
                        'outline',
                        'subtle'
                    ],
                    class: ''
                },
                {
                    color: 'neutral',
                    highlight: true,
                    class: 'ring ring-inset ring-inverted'
                },
                {
                    leading: true,
                    size: 'xs',
                    class: 'ps-7'
                },
                {
                    leading: true,
                    size: 'sm',
                    class: 'ps-8'
                },
                {
                    leading: true,
                    size: 'md',
                    class: 'ps-9'
                },
                {
                    leading: true,
                    size: 'lg',
                    class: 'ps-10'
                },
                {
                    leading: true,
                    size: 'xl',
                    class: 'ps-11'
                },
                {
                    trailing: true,
                    size: 'xs',
                    class: 'pe-7'
                },
                {
                    trailing: true,
                    size: 'sm',
                    class: 'pe-8'
                },
                {
                    trailing: true,
                    size: 'md',
                    class: 'pe-9'
                },
                {
                    trailing: true,
                    size: 'lg',
                    class: 'pe-10'
                },
                {
                    trailing: true,
                    size: 'xl',
                    class: 'pe-11'
                },
                {
                    loading: true,
                    leading: true,
                    class: {
                        leadingIcon: 'animate-spin'
                    }
                },
                {
                    loading: true,
                    leading: false,
                    trailing: true,
                    class: {
                        trailingIcon: 'animate-spin'
                    }
                }
            ],
            defaultVariants: {
                color: '',
                variant: 'outline'
            }
        }
    }
})

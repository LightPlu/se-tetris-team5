package se.tetris.team5.items;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.blocks.Block;
import se.tetris.team5.blocks.IBlock;
import se.tetris.team5.items.ItemGrantPolicy.ItemGrantContext;

/**
 * ItemGrantPolicy 인터페이스 테스트
 */
public class ItemGrantPolicyTest {

    private ItemFactory itemFactory;
    private Block testBlock;

    @Before
    public void setUp() {
        itemFactory = new ItemFactory();
        testBlock = new IBlock();
    }

    /**
     * 테스트 1: ItemGrantContext 생성
     */
    @Test
    public void testItemGrantContext_Creation() {
        ItemGrantContext context = new ItemGrantContext(10, itemFactory);
        
        assertNotNull("컨텍스트가 생성되어야 함", context);
        assertEquals("줄 수가 설정되어야 함", 10, context.totalClearedLines);
        assertNotNull("아이템 팩토리가 설정되어야 함", context.itemFactory);
    }

    /**
     * 테스트 2: ItemGrantContext - 0줄
     */
    @Test
    public void testItemGrantContext_ZeroLines() {
        ItemGrantContext context = new ItemGrantContext(0, itemFactory);
        
        assertEquals("0줄로 생성 가능", 0, context.totalClearedLines);
    }

    /**
     * 테스트 3: ItemGrantContext - 음수 줄
     */
    @Test
    public void testItemGrantContext_NegativeLines() {
        ItemGrantContext context = new ItemGrantContext(-5, itemFactory);
        
        assertEquals("음수 줄로 생성 가능", -5, context.totalClearedLines);
    }

    /**
     * 테스트 4: ItemGrantContext - null ItemFactory
     */
    @Test
    public void testItemGrantContext_NullFactory() {
        ItemGrantContext context = new ItemGrantContext(10, null);
        
        assertNull("null 팩토리로 생성 가능", context.itemFactory);
    }

    /**
     * 테스트 5: ItemGrantContext - 큰 줄 수
     */
    @Test
    public void testItemGrantContext_LargeLines() {
        ItemGrantContext context = new ItemGrantContext(999999, itemFactory);
        
        assertEquals("큰 줄 수로 생성 가능", 999999, context.totalClearedLines);
    }

    /**
     * 테스트 6: 기본 reset 메서드
     */
    @Test
    public void testItemGrantPolicy_DefaultReset() {
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                return null;
            }
        };
        
        policy.reset(); // 예외 발생하지 않아야 함
        assertTrue("기본 reset 메서드가 안전해야 함", true);
    }

    /**
     * 테스트 7: 익명 구현 - null 반환
     */
    @Test
    public void testItemGrantPolicy_AnonymousImplNull() {
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                return null;
            }
        };
        
        ItemGrantContext context = new ItemGrantContext(10, itemFactory);
        Item item = policy.grantItem(testBlock, context);
        
        assertNull("null을 반환해야 함", item);
    }

    /**
     * 테스트 8: 익명 구현 - 아이템 부여
     */
    @Test
    public void testItemGrantPolicy_AnonymousImplGrantItem() {
        final Item testItem = new DoubleScoreItem();
        
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                if (block != null && context != null) {
                    block.setItem(0, 0, testItem);
                    return testItem;
                }
                return null;
            }
        };
        
        ItemGrantContext context = new ItemGrantContext(10, itemFactory);
        Item result = policy.grantItem(testBlock, context);
        
        assertNotNull("아이템이 부여되어야 함", result);
        assertEquals("부여된 아이템이 일치해야 함", testItem, result);
    }

    /**
     * 테스트 9: 익명 구현 - null 블록 처리
     */
    @Test
    public void testItemGrantPolicy_AnonymousImplNullBlock() {
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                if (block == null) {
                    return null;
                }
                return new DoubleScoreItem();
            }
        };
        
        ItemGrantContext context = new ItemGrantContext(10, itemFactory);
        Item result = policy.grantItem(null, context);
        
        assertNull("null 블록에는 아이템이 부여되지 않아야 함", result);
    }

    /**
     * 테스트 10: 익명 구현 - null 컨텍스트 처리
     */
    @Test
    public void testItemGrantPolicy_AnonymousImplNullContext() {
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                if (context == null) {
                    return null;
                }
                return new DoubleScoreItem();
            }
        };
        
        Item result = policy.grantItem(testBlock, null);
        
        assertNull("null 컨텍스트에는 아이템이 부여되지 않아야 함", result);
    }

    /**
     * 테스트 11: ItemGrantContext - 여러 인스턴스
     */
    @Test
    public void testItemGrantContext_MultipleInstances() {
        ItemGrantContext ctx1 = new ItemGrantContext(10, itemFactory);
        ItemGrantContext ctx2 = new ItemGrantContext(20, itemFactory);
        ItemGrantContext ctx3 = new ItemGrantContext(30, itemFactory);
        
        assertEquals("각 인스턴스가 독립적이어야 함", 10, ctx1.totalClearedLines);
        assertEquals("각 인스턴스가 독립적이어야 함", 20, ctx2.totalClearedLines);
        assertEquals("각 인스턴스가 독립적이어야 함", 30, ctx3.totalClearedLines);
    }

    /**
     * 테스트 12: ItemGrantContext - 필드 수정
     */
    @Test
    public void testItemGrantContext_FieldModification() {
        ItemGrantContext context = new ItemGrantContext(10, itemFactory);
        
        context.totalClearedLines = 50;
        assertEquals("필드를 수정할 수 있어야 함", 50, context.totalClearedLines);
        
        ItemFactory newFactory = new ItemFactory();
        context.itemFactory = newFactory;
        assertEquals("팩토리를 변경할 수 있어야 함", newFactory, context.itemFactory);
    }

    /**
     * 테스트 13: 여러 정책 구현 - 조건부 부여
     */
    @Test
    public void testItemGrantPolicy_ConditionalGrant() {
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                if (context != null && context.totalClearedLines >= 10) {
                    return new DoubleScoreItem();
                }
                return null;
            }
        };
        
        ItemGrantContext ctx1 = new ItemGrantContext(5, itemFactory);
        ItemGrantContext ctx2 = new ItemGrantContext(15, itemFactory);
        
        assertNull("조건 미달 시 null", policy.grantItem(testBlock, ctx1));
        assertNotNull("조건 충족 시 아이템 부여", policy.grantItem(testBlock, ctx2));
    }

    /**
     * 테스트 14: reset 오버라이드
     */
    @Test
    public void testItemGrantPolicy_ResetOverride() {
        final boolean[] resetCalled = {false};
        
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                return null;
            }
            
            @Override
            public void reset() {
                resetCalled[0] = true;
            }
        };
        
        policy.reset();
        assertTrue("reset이 호출되어야 함", resetCalled[0]);
    }

    /**
     * 테스트 15: 여러 번 reset 호출
     */
    @Test
    public void testItemGrantPolicy_MultipleResets() {
        final int[] resetCount = {0};
        
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                return null;
            }
            
            @Override
            public void reset() {
                resetCount[0]++;
            }
        };
        
        policy.reset();
        policy.reset();
        policy.reset();
        
        assertEquals("reset이 3번 호출되어야 함", 3, resetCount[0]);
    }

    /**
     * 테스트 16: grantItem 여러 번 호출
     */
    @Test
    public void testItemGrantPolicy_MultipleGrants() {
        final int[] grantCount = {0};
        
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                grantCount[0]++;
                return new DoubleScoreItem();
            }
        };
        
        ItemGrantContext context = new ItemGrantContext(10, itemFactory);
        
        for (int i = 0; i < 10; i++) {
            policy.grantItem(testBlock, context);
        }
        
        assertEquals("grantItem이 10번 호출되어야 함", 10, grantCount[0]);
    }

    /**
     * 테스트 17: 다양한 아이템 타입 부여
     */
    @Test
    public void testItemGrantPolicy_VariousItemTypes() {
        Item[] items = {
            new DoubleScoreItem(),
            new LineClearItem(),
            new WeightBlockItem()
        };
        
        for (final Item item : items) {
            ItemGrantPolicy policy = new ItemGrantPolicy() {
                @Override
                public Item grantItem(Block block, ItemGrantContext context) {
                    return item;
                }
            };
            
            ItemGrantContext context = new ItemGrantContext(10, itemFactory);
            Item result = policy.grantItem(testBlock, context);
            
            assertEquals("부여된 아이템이 일치해야 함", item, result);
        }
    }

    /**
     * 테스트 18: ItemGrantContext - totalClearedLines 경계값
     */
    @Test
    public void testItemGrantContext_BoundaryValues() {
        ItemGrantContext ctx1 = new ItemGrantContext(Integer.MAX_VALUE, itemFactory);
        ItemGrantContext ctx2 = new ItemGrantContext(Integer.MIN_VALUE, itemFactory);
        
        assertEquals("최대값으로 생성 가능", Integer.MAX_VALUE, ctx1.totalClearedLines);
        assertEquals("최소값으로 생성 가능", Integer.MIN_VALUE, ctx2.totalClearedLines);
    }

    /**
     * 테스트 19: 정책 체인 - 순차 실행
     */
    @Test
    public void testItemGrantPolicy_Chain() {
        ItemGrantPolicy policy1 = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                if (context.totalClearedLines < 10) {
                    return new DoubleScoreItem();
                }
                return null;
            }
        };
        
        ItemGrantPolicy policy2 = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                if (context.totalClearedLines >= 10) {
                    return new LineClearItem();
                }
                return null;
            }
        };
        
        ItemGrantContext ctx1 = new ItemGrantContext(5, itemFactory);
        ItemGrantContext ctx2 = new ItemGrantContext(15, itemFactory);
        
        Item result1 = policy1.grantItem(testBlock, ctx1);
        if (result1 == null) {
            result1 = policy2.grantItem(testBlock, ctx1);
        }
        
        Item result2 = policy1.grantItem(testBlock, ctx2);
        if (result2 == null) {
            result2 = policy2.grantItem(testBlock, ctx2);
        }
        
        assertNotNull("policy1이 처리해야 함", result1);
        assertNotNull("policy2가 처리해야 함", result2);
    }

    /**
     * 테스트 20: grantItem 반환값 일관성
     */
    @Test
    public void testItemGrantPolicy_ConsistentReturn() {
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                return new DoubleScoreItem();
            }
        };
        
        ItemGrantContext context = new ItemGrantContext(10, itemFactory);
        
        Item item1 = policy.grantItem(testBlock, context);
        Item item2 = policy.grantItem(testBlock, context);
        
        assertNotNull("아이템이 부여되어야 함", item1);
        assertNotNull("아이템이 부여되어야 함", item2);
    }

    /**
     * 테스트 21: 정책별 상태 관리
     */
    @Test
    public void testItemGrantPolicy_StateManagement() {
        final int[] internalState = {0};
        
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                internalState[0]++;
                if (internalState[0] % 2 == 0) {
                    return new DoubleScoreItem();
                }
                return null;
            }
            
            @Override
            public void reset() {
                internalState[0] = 0;
            }
        };
        
        ItemGrantContext context = new ItemGrantContext(10, itemFactory);
        
        assertNull("첫 호출은 null", policy.grantItem(testBlock, context));
        assertNotNull("두 번째 호출은 아이템", policy.grantItem(testBlock, context));
        
        policy.reset();
        assertEquals("reset 후 상태가 초기화되어야 함", 0, internalState[0]);
    }

    /**
     * 테스트 22: 블록 타입별 처리
     */
    @Test
    public void testItemGrantPolicy_BlockTypeSpecific() {
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                if (block != null && block.width() == 4) {
                    return new DoubleScoreItem();
                }
                return null;
            }
        };
        
        ItemGrantContext context = new ItemGrantContext(10, itemFactory);
        Item result = policy.grantItem(testBlock, context); // IBlock은 width=4
        
        assertNotNull("IBlock에 아이템이 부여되어야 함", result);
    }

    /**
     * 테스트 23: 컨텍스트 필드 접근
     */
    @Test
    public void testItemGrantPolicy_ContextFieldAccess() {
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                if (context != null && context.itemFactory != null) {
                    return context.itemFactory.createRandomItem();
                }
                return null;
            }
        };
        
        ItemGrantContext context = new ItemGrantContext(10, itemFactory);
        Item result = policy.grantItem(testBlock, context);
        
        assertNotNull("팩토리로 아이템이 생성되어야 함", result);
    }

    /**
     * 테스트 24: null 안전성 - 모든 인자 null
     */
    @Test
    public void testItemGrantPolicy_AllNullSafety() {
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                if (block == null || context == null) {
                    return null;
                }
                return new DoubleScoreItem();
            }
        };
        
        assertNull("모두 null이면 null 반환", policy.grantItem(null, null));
    }

    /**
     * 테스트 25: 컨텍스트 재사용
     */
    @Test
    public void testItemGrantPolicy_ContextReuse() {
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                return new DoubleScoreItem();
            }
        };
        
        ItemGrantContext context = new ItemGrantContext(10, itemFactory);
        
        Item item1 = policy.grantItem(testBlock, context);
        Item item2 = policy.grantItem(testBlock, context);
        Item item3 = policy.grantItem(testBlock, context);
        
        assertNotNull("첫 번째 호출", item1);
        assertNotNull("두 번째 호출", item2);
        assertNotNull("세 번째 호출", item3);
    }

    /**
     * 테스트 26: 정책 조합
     */
    @Test
    public void testItemGrantPolicy_Combination() {
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                if (context.totalClearedLines > 0 && context.totalClearedLines % 5 == 0) {
                    return new DoubleScoreItem();
                }
                return null;
            }
        };
        
        assertNull("4줄에는 부여 안됨", policy.grantItem(testBlock, new ItemGrantContext(4, itemFactory)));
        assertNotNull("5줄에는 부여됨", policy.grantItem(testBlock, new ItemGrantContext(5, itemFactory)));
        assertNotNull("10줄에는 부여됨", policy.grantItem(testBlock, new ItemGrantContext(10, itemFactory)));
    }

    /**
     * 테스트 27: ItemGrantContext - null 필드 변경
     */
    @Test
    public void testItemGrantContext_NullFieldChange() {
        ItemGrantContext context = new ItemGrantContext(10, itemFactory);
        context.itemFactory = null;
        
        assertNull("팩토리를 null로 변경 가능", context.itemFactory);
    }

    /**
     * 테스트 28: 예외 처리
     */
    @Test
    public void testItemGrantPolicy_ExceptionHandling() {
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                try {
                    if (block.width() > 0) {
                        return new DoubleScoreItem();
                    }
                } catch (Exception e) {
                    return null;
                }
                return null;
            }
        };
        
        Item result = policy.grantItem(testBlock, new ItemGrantContext(10, itemFactory));
        assertNotNull("예외가 발생하지 않아야 함", result);
    }

    /**
     * 테스트 29: 정책 우선순위
     */
    @Test
    public void testItemGrantPolicy_Priority() {
        final int[] priority = {1};
        
        ItemGrantPolicy policy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                if (priority[0] == 1 && context.totalClearedLines >= 10) {
                    return new DoubleScoreItem();
                }
                return null;
            }
        };
        
        ItemGrantContext context = new ItemGrantContext(15, itemFactory);
        
        assertNotNull("우선순위 1일 때 부여", policy.grantItem(testBlock, context));
        
        priority[0] = 2;
        assertNull("우선순위 변경 후 부여 안됨", policy.grantItem(testBlock, context));
    }

    /**
     * 테스트 30: 동일 컨텍스트로 여러 정책 실행
     */
    @Test
    public void testItemGrantPolicy_SameContextMultiplePolicies() {
        ItemGrantPolicy policy1 = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                return new DoubleScoreItem();
            }
        };
        
        ItemGrantPolicy policy2 = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                return new LineClearItem();
            }
        };
        
        ItemGrantContext context = new ItemGrantContext(10, itemFactory);
        
        Item item1 = policy1.grantItem(testBlock, context);
        Item item2 = policy2.grantItem(testBlock, context);
        
        assertNotNull("policy1이 아이템 부여", item1);
        assertNotNull("policy2가 아이템 부여", item2);
        assertTrue("다른 타입의 아이템", item1.getClass() != item2.getClass());
    }
}

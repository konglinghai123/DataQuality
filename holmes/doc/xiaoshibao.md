## 基本信息
- rpt_yuce_hourly_report    data_hour/yyyy-MM-dd HH
- 数据条数： 280-886
- 多维度指标：

|维度|指标|
|------|------|
| ip_country="Saudi Arabia"  siteid=600,900 | sum(dau)  11415-220316    sum(revenue)    2494-1163185    sum(paid_order_num) 25-7973 |
| ip_country="Bahrain", "Jordan", "Kuwait", "Lebanon", "Oman", "Qatar", "Saudi Arabia", "United Arab Emirates"  siteid=600,900 | sum(dau) 15389-279746 sum(revenue) 3174-1446353 sum(paid_order_num) 31-9939 |

- 同比（昨天）： sum(dau)    sum(access_1)   sum(cart)   sum(order_user_num)   sum(paid_order_num)   sum(order_num)   sum(revenue)   sum(goods_revenue)   sum(goods_cost)

## dataVolumn
```json
   {
      "ruleName": "rpt_yuce_hourly_report_dataVolume",
      "ruleType": "dataVolume",
      "sourceInfo": {
        "sourceConnectionName": "zydb",
        "sourceTableName": "rpt_yuce_hourly_report",
        "partitionName": "data_hour",
        "partitionValueFormat": "yyyy-MM-dd HH"
      },
      "ruleInfo": {
        "operator": "<",
        "value": 280
      },
      "ruleDescription": "",
      "alarmType": "both",
      "alarmUser": {
        "user": [
          "1199",
          "1228",
          "hanshizhong"
        ],
        "email": [
          "evan@jollycorp.com",
          "zizi@jollycorp.com",
          "hanshizhong@jollycorp.com"
        ]
      }
    }
```

## keyIndicatorWithDimension
```json
    {
      "ruleName": "rpt_yuce_hourly_report_keyIndicatorWithDimension",
      "ruleType": "keyIndicatorWithDimension",
      "sourceInfo": {
        "sourceConnectionName": "zydb",
        "sourceTableName": "rpt_yuce_hourly_report",
        "partitionName": "data_hour",
        "partitionValueFormat": "yyyy-MM-dd HH"
      },
      "ruleInfo": {
        "conditions": [
          {
            "dimensions": [
              {
                "dimensionName": "ip_country",
                "dimensionValues": [
                  "Saudi Arabia"
                ]
              },
              {
                "dimensionName": "siteid",
                "dimensionValues": [
                  "600",
                  "900"
                ]
              }
            ],
            "columns": [
              {
                "columnName": "dau",
                "aggregation": "sum",
                "operator": "<",
                "value": 11415
              },
              {
                "columnName": "revenue",
                "aggregation": "sum",
                "operator": "<",
                "value": 2494
              },
              {
                "columnName": "paid_order_num",
                "aggregation": "sum",
                "operator": "<",
                "value": 25
              }
            ]
          },
          {
            "dimensions": [
              {
                "dimensionName": "ip_country",
                "dimensionValues": [
                  "Bahrain",
                  "Jordan",
                  "Kuwait",
                  "Lebanon",
                  "Oman",
                  "Qatar",
                  "Saudi Arabia",
                  "United Arab Emirates"
                ]
              },
              {
                "dimensionName": "siteid",
                "dimensionValues": [
                  "600",
                  "900"
                ]
              }
            ],
            "columns": [
              {
                "columnName": "dau",
                "aggregation": "sum",
                "operator": "<",
                "value": 15389
              },
              {
                "columnName": "revenue",
                "aggregation": "sum",
                "operator": "<",
                "value": 3174
              },
              {
                "columnName": "paid_order_num",
                "aggregation": "sum",
                "operator": "<",
                "value": 31
              }
            ]
          }
        ]
      },
      "ruleDescription": "",
      "alarmType": "both",
      "alarmUser": {
        "user": [
          "1199",
          "1228",
          "hanshizhong"
        ],
        "email": [
          "evan@jollycorp.com",
          "zizi@jollycorp.com",
          "hanshizhong@jollycorp.com"
        ]
      }
    }
```

## comparisonToTheSameTime
```json
  {
      "ruleName": "rpt_yuce_hourly_report_comparisonToTheSameTime",
      "ruleType": "comparisonToTheSameTime",
      "sourceInfo": {
        "sourceConnectionName": "zydb",
        "sourceTableName": "rpt_yuce_hourly_report",
        "partitionName": "data_hour",
        "partitionValueFormat": "yyyy-MM-dd HH"
      },
      "ruleInfo": {
        "conditions": [
          {
            "dimensions": [],
            "columns": [
              {
                "columnName": "dau",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "access_1",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "cart",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "order_user_num",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "paid_order_num",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "order_num",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "revenue",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "goods_revenue",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "goods_cost",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              }
            ]
          },
          {
            "dimensions": [
              {
                "dimensionName": "ip_country",
                "dimensionValues": [
                  "Bahrain",
                  "Jordan",
                  "Kuwait",
                  "Lebanon",
                  "Oman",
                  "Qatar",
                  "Saudi Arabia",
                  "United Arab Emirates"
                ]
              },
              {
                "dimensionName": "siteid",
                "dimensionValues": [
                  "600",
                  "900"
                ]
              }
            ],
            "columns": [
              {
                "columnName": "dau",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "access_1",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "cart",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "order_user_num",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "paid_order_num",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "order_num",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "revenue",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "goods_revenue",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "goods_cost",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              }
            ]
          },
          {
            "dimensions": [
              {
                "dimensionName": "ip_country",
                "dimensionValues": [
                  "Saudi Arabia"
                ]
              },
              {
                "dimensionName": "siteid",
                "dimensionValues": [
                  "600",
                  "900"
                ]
              }
            ],
            "columns": [
              {
                "columnName": "dau",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "access_1",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "cart",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "order_user_num",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "paid_order_num",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "order_num",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "revenue",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "goods_revenue",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "goods_cost",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              }
            ]
          },
          {
            "dimensions": [
              {
                "dimensionName": "ip_country",
                "dimensionValues": [
                  "Indonesia"
                ]
              },
              {
                "dimensionName": "siteid",
                "dimensionValues": [
                  "600",
                  "900"
                ]
              }
            ],
            "columns": [
              {
                "columnName": "dau",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "paid_order_num",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.1
              }
            ]
          },
          {
            "dimensions": [
              {
                "dimensionName": "ip_country",
                "dimensionValues": [
                  "United Arab Emirates"
                ]
              },
              {
                "dimensionName": "siteid",
                "dimensionValues": [
                  "600",
                  "900"
                ]
              }
            ],
            "columns": [
              {
                "columnName": "dau",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "paid_order_num",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.1
              }
            ]
          },
          {
            "dimensions": [
              {
                "dimensionName": "ip_country",
                "dimensionValues": [
                  "Bahrain"
                ]
              },
              {
                "dimensionName": "siteid",
                "dimensionValues": [
                  "600",
                  "900"
                ]
              }
            ],
            "columns": [
              {
                "columnName": "dau",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              }
            ]
          },
          {
            "dimensions": [
              {
                "dimensionName": "ip_country",
                "dimensionValues": [
                  "Jordan"
                ]
              },
              {
                "dimensionName": "siteid",
                "dimensionValues": [
                  "600",
                  "900"
                ]
              }
            ],
            "columns": [
              {
                "columnName": "dau",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "paid_order_num",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.1
              }
            ]
          },
          {
            "dimensions": [
              {
                "dimensionName": "ip_country",
                "dimensionValues": [
                  "Kuwait"
                ]
              },
              {
                "dimensionName": "siteid",
                "dimensionValues": [
                  "600",
                  "900"
                ]
              }
            ],
            "columns": [
              {
                "columnName": "dau",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              },
              {
                "columnName": "paid_order_num",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.1
              }
            ]
          },
          {
            "dimensions": [
              {
                "dimensionName": "ip_country",
                "dimensionValues": [
                  "Oman"
                ]
              },
              {
                "dimensionName": "siteid",
                "dimensionValues": [
                  "600",
                  "900"
                ]
              }
            ],
            "columns": [
              {
                "columnName": "dau",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              }
            ]
          },
          {
            "dimensions": [
              {
                "dimensionName": "ip_country",
                "dimensionValues": [
                  "Qatar"
                ]
              },
              {
                "dimensionName": "siteid",
                "dimensionValues": [
                  "600",
                  "900"
                ]
              }
            ],
            "columns": [
              {
                "columnName": "dau",
                "aggregation": "sum",
                "operator": "<",
                "percent": 0.5
              }
            ]
          }
        ],
        "differDay": 1
      },
      "ruleDescription": "",
      "alarmType": "both",
      "alarmUser": {
        "user": [
          "1199",
          "1228",
          "hanshizhong",
          "baopuzi",
          "1056",
          "zhaoxin"
        ],
        "email": [
          "evan@jollycorp.com",
          "zizi@jollycorp.com",
          "hanshizhong@jollycorp.com",
          "baopuzi@jollycorp.com",
          "rocky@jollycorp.com",
          "zhaoxin@jollycorp.com"
        ]
      }
    }
```